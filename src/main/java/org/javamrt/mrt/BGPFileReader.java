// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.progs.route_btoa;
import org.javamrt.utils.Debug;
import org.javamrt.utils.RecordAccess;

import java.io.*;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

public class BGPFileReader {
	private static final boolean debug = false;

	private BufferedInputStream in = null;
	private LinkedList<MRTRecord> recordFifo;

	private boolean eof = false;

	private byte[] header;
	private byte[] record;

	private String toString;
	/*****
	 *
	 * public BGPFileReader (BufferedInputStream in)
	 *
	 * create a new BGPFileReader from BufferedInputStream <in>
	 */

	public BGPFileReader(BufferedInputStream in) {
		this.in = in;
		this.toString = in.toString();
		this.recordFifo = new LinkedList<MRTRecord>();
		this.header = new byte[12]; // always 12 bytes, create once
		this.record = null;
		this.eof = false;
	}

	/*****
	 *
	 * public BGPFileReader (String name)
	 *
	 * create a new BGPFileReader from BufferedInputStream specified by the
	 * String name
	 * @throws Exception
	 */

	public BGPFileReader(String name) throws Exception  {
		InputStream inStream = null;
		this.toString = name;

		try { // to open the name as a URL
			java.net.URL url = new java.net.URL(name);
			inStream = url.openStream();
		} catch (java.net.MalformedURLException e) {
			File inFile = new File(name);
			if (!inFile.exists())
				throw new java.io.FileNotFoundException(name);
			inStream = new FileInputStream(inFile);
		}


		if (this.toString.endsWith(".gz")) {
			this.in = new BufferedInputStream(new GZIPInputStream(inStream));
		} else {
			this.in = new BufferedInputStream(inStream);
		}

		this.recordFifo = new LinkedList<MRTRecord>();
		this.header = new byte[12]; // always 12 bytes, create once
		this.record = null;
		this.eof = false;
	}

	public BGPFileReader(File f) throws IOException {
		if (!f.exists())
			throw new java.io.FileNotFoundException();
		FileInputStream inStream = new FileInputStream(f);
		this.toString = f.getCanonicalPath();
		if (this.toString.endsWith(".gz")) {
			this.in = new BufferedInputStream(new GZIPInputStream(inStream));
		} else {
			this.in = new BufferedInputStream(inStream);
		}
		this.recordFifo = new LinkedList<MRTRecord>();
		this.header = new byte[12]; // always 12 bytes, create once
		this.record = null;
		this.eof = false;
	}

	/***
	 * void close()
	 *
	 * close the BGPFileReader
	 */
	public void close() throws java.io.IOException {
		this.in.close();
		this.recordFifo.clear();
		this.recordFifo = null;
		this.header = null;
		this.record = null;
	}

	/**
	 * toString(): return the name of the input Stream
	 */
	public String toString() {
		return this.toString;
	}
	/***
	 *
	 * MRTRecord readNext()
	 *
	 * returns next record on successful completion null on EOF
	 *
	 * throws Exception when something goes wrong
	 */

	private int type = 0;
	private int subtype = 0;
	private long time = 0;

	private long recordCounter = 0;

	/**
	 * @return the number of MRT binary format records read.<br>
	 * In the new MRT record formats, that has little or nothing<br>
	 * to do with the number of BGP Events read, since a record might<br>
	 * hold several BGP events. We need this in our quest for soft session<br>
	 * restarts.
	 */
	public long mrtRecords() {
		return recordCounter;
	}

	/**
	 * The name MRT record is not perfect, because actually it's routing events we get
	 * @return a BGP event or a BGP control message.
	 * @throws Exception
	 */
	public MRTRecord readNext() throws Exception {
		while (true) {
			/*
			 * Consume any records waiting in the queue
			 *
			 * using recordFifo.add(MRTRecord) <=> recordFifo.remove()
			 */
			if (recordFifo.size() != 0)
				return recordFifo.remove();

			/*
			 * Help GC
			 */
			if (record != null)
				record = null;
			/*
			 * if the queue is empty, read from the file
			 */

			int bytesRead = readFromInputStream(this.in, header, header.length);
			recordCounter ++;
			/*
			 * EOF
			 */
			if (bytesRead <= 0) {
				this.eof = true;
				return null;
			}
			/*
			 * truncated
			 */
			if (bytesRead != this.header.length) {
				this.eof = true;
				throw new BGPFileReaderException("Truncated file: " + bytesRead
						+ " instead of " + this.header.length + " bytes", header);
			}
			if (Debug.compileDebug)
				RecordAccess.dump(Debug.debugStream, header);
			time = RecordAccess.getU32(header, 0);
			type = RecordAccess.getU16(header, 4);
			subtype = RecordAccess.getU16(header, 6);

			final long recordlen = RecordAccess.getU32(header, 8);

			if (Debug.compileDebug) Debug.println("TIME: " + time + "\n TYPE: " + type
						+ "\n SUBTYPE: " + subtype + "\n RECORDLENGTH: "
						+ recordlen);

			if (recordlen > Integer.MAX_VALUE) throw new RuntimeException("Can't have a record longer than 2147483647 bytes");
			this.record = new byte[(int) recordlen];

			bytesRead = readFromInputStream(this.in, record, record.length);

			if (bytesRead != this.record.length) {
				this.eof = true;
				throw new BGPFileReaderException("Truncated file: " + bytesRead
						+ " instead of " + this.record.length + " bytes", header);
			}

			if (Debug.compileDebug)
				Debug.dump(this.record);
			/*
			 * Record parsing
			 */
			switch (type) {
				case MRTConstants.TABLE_DUMP:
					return parseTableDump(subtype);

				case MRTConstants.TABLE_DUMP_v2:

					switch (subtype) {
						case MRTConstants.PEER_INDEX_TABLE:
							parsePeerIndexTable();
							break;
						case 2:
							parseTableDumpv2(MRTConstants.AFI_IPv4);
							break;
						case 4:
							parseTableDumpv2(MRTConstants.AFI_IPv6);
							break;
						case 6:
							parseGenericRib();
							break;
						case 3:
						case 5:
							parseTableDumpv2Multicast();
							break;
						default:
							throw new BGPFileReaderException(
									"Unknown TABLE_DUMP_V2 subtype" + subtype, header);
					}
					break;

				case MRTConstants.BGP4MP:
					MRTRecord bgp4mp = parseBgp4mp(subtype);
					if ((bgp4mp) != null) {
						return bgp4mp;
					}
					break;

                case MRTConstants.BGPDUMP_TYPE_MRTD_BGP:
                    MRTRecord bgpRecord = parseBgp(subtype);
                    if ((bgpRecord) != null) {
                        return bgpRecord;
                    }
                    break;
                default:
                    return new MRTRecord(header, record);
			}
		}
	}

	/**
	 * Safely read from input streams that can be blocked or slow (e.g. compressed streams).
	 * @return the total of bytes read from the input stream or -1 if <code>EOF</code> is first found
	 */
	private static int readFromInputStream(BufferedInputStream bis, byte[] output, int length) throws IOException {
		int remaining = length;
		int read;

		while (((read = bis.read(output, length - remaining, remaining)) > 0) && ((remaining -= read) > 0));

		return ((read == -1 && remaining == length) ? -1 : length - remaining);
	}

	private MRTRecord parseTableDump(int subtype) throws Exception {
		switch (subtype) {
		case MRTConstants.AFI_IPv4:
		case MRTConstants.AFI_IPv6:
			return new TableDump(header, record, subtype);
		default:
			throw new BGPFileReaderException("Unknown TABLE_DUMP subtype"
					+ subtype, header);
		}

	}

	private MRTRecord parseBgp(int subtype) throws Exception {
        //Based on https://tools.ietf.org/html/rfc1771
        switch (subtype){
            case MRTConstants.BGPDUMP_SUBTYPE_MRTD_BGP_UPDATE:
                return getBgpUpdate();
            case MRTConstants.BGPDUMP_SUBTYPE_MRTD_BGP_KEEPALIVE:
                return new KeepAlive(header, record);
            case MRTConstants.BGPDUMP_SUBTYPE_MRTD_BGP_STATE_CHANGE:
                int offset = 0;
                int asSize = 2;
                int addrSize = 4;

                AS srcAs = new AS(RecordAccess.getUINT(record, offset, asSize));
                offset = asSize;

                InetAddress srcIP = InetAddress.getByAddress(RecordAccess.getBytes(record, offset, addrSize));
                offset += addrSize;

                int oldState = RecordAccess.getU16(record, offset);
                offset += 2;
                int newState = RecordAccess.getU16(record, offset);

                return new StateChange(
                        RecordAccess.getU32(header, 0),
                        srcIP,
                        srcAs,
                        oldState,
                        newState,
                        MRTConstants.UPDATE_STR_BGP,
                        header,
                        record);
            default:
                return new MRTRecord(header, record);
        }
    }

    private MRTRecord getBgpUpdate() throws Exception{
        int offset = 0;
        int asSize = 2;
        int addrSize = 4;

        AS srcAs = new AS(RecordAccess.getUINT(record, offset, asSize));
        offset = asSize;

        InetAddress srcIP = InetAddress.getByAddress(RecordAccess.getBytes(record, offset, addrSize));
        offset += addrSize;

        AS dstAs = new AS(RecordAccess.getUINT(record, offset, asSize));
        offset +=asSize;

        InetAddress dstIP = InetAddress.getByAddress(RecordAccess.getBytes(record, offset, addrSize));
        offset += addrSize;

        long wSize = RecordAccess.getUINT(record, offset, 2);
        offset += 2;

        // WITHDRAWS
        if(wSize > 0) { // Withdraw if the size > than 0.
            for (int i = 0; i < wSize;) {
                Nlri wNlri = new Nlri(record, offset, 1);
                offset += wNlri.getOffset();
                i += wNlri.getOffset();

                Withdraw withdraw = new Withdraw(header, record, srcIP, srcAs, wNlri.toPrefix(),MRTConstants.UPDATE_STR_BGP);
                recordFifo.add(withdraw);
            }
        }else{ // Advertisments

            //Reading length of attributes
            int attrLen = RecordAccess.getU16(record, offset);
            offset +=2;

            if(attrLen > 0){

                Attributes attributes = null;
                try {
                    attributes = new Attributes(record, attrLen, offset,asSize);
                } catch (RFC4893Exception rfce) {
                    //
                    // piggyback peer and time info
                    //
                    rfce.setTimestamp(this.time);
                    rfce.setPeer(srcIP);
                    rfce.setAS(srcAs);
                    throw rfce;
                } catch (Exception e) {
                    throw e;
                }

                //Process MP_REACH and MP_UNREACH
                processReachAndUnreach(attributes, srcIP, srcAs, MRTConstants.UPDATE_STR_BGP);

                offset += attrLen;
                while (offset < record.length) {
                    Nlri aNlri = new Nlri(record, offset, 1);
                    offset += aNlri.getOffset();

                    recordFifo.add(new Advertisement(header, record, srcIP, srcAs,
                            aNlri.toPrefix(), attributes, MRTConstants.UPDATE_STR_BGP));
                }
            }
        }

        return recordFifo.remove();
    }

	private MRTRecord parseBgp4mp(int subtype) throws Exception {
		// route_btoa.System_err_println("parseBgp4mp("+MRTConstants.mpSubType(subtype)+")");
		switch (subtype) {
		case MRTConstants.BGP4MP_MESSAGE:
		case MRTConstants.BGP4MP_MESSAGE_AS4:
			return parseBgp4Update((subtype == MRTConstants.BGP4MP_MESSAGE) ? 2
					: 4);

			/*
			 * TODO
			 * TTOODDOO::::
			 *
			 *
			 * case BGP4MP_SNAPSHOT: return new Bgp4mpSnapshot(header,record);
			 */

		case MRTConstants.BGP4MP_ENTRY:
			return parseBgp4Entry(RecordAccess.getU16(record, 6));

		case MRTConstants.BGP4MP_STATE_CHANGE: {
			/*
			 * 0 1 2 3 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8
			 * 9 0 1
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * | Peer AS number | Local AS number |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * | Interface Index | Address Family |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * | Peer IP address (variable) |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * | Local IP address (variable) |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * | Old State | New State |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 */

			int afi = RecordAccess.getU16(record, 6);
			int addrOffs = 8;
			int addrSize = (afi == MRTConstants.AFI_IPv4) ? 4 : 16;
			int stateOffs = addrOffs + 2 * addrSize;
			int oldState = RecordAccess.getU16(record, stateOffs);
			stateOffs += 2;
			int newState = RecordAccess.getU16(record, stateOffs);

			return new StateChange(
					RecordAccess.getU32(header, 0),
					InetAddress.getByAddress(RecordAccess.getBytes(record, addrOffs, addrSize)),
					new AS(RecordAccess.getU16(record, 0)),
					oldState,
					newState,
                    MRTConstants.UPDATE_STR_BGP4MP,
					header,
					record);

		}

		case MRTConstants.BGP4MP_STATE_CHANGE_AS4: {
			/*
			 * draft-ietf-grow-mrt-07.txt
			 *
			 * 0 1 2 3 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8
			 * 9 0 1
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * | Peer AS number |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * | Local AS number |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * | Interface Index | Address Family |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * | Peer IP address (variable) |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * | Local IP address (variable) |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * | Old State | New State |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 */

			int afi = RecordAccess.getU16(record, 10);
			int addrSize = (afi == MRTConstants.AFI_IPv4) ? 4 : 16;
			int addrOffs = 12;
			int stateOffs = addrOffs + 2 * addrSize;
			int oldState = RecordAccess.getU16(record, stateOffs);
			stateOffs += 2;
			int newState = RecordAccess.getU16(record, stateOffs);

			return new StateChange(
					RecordAccess.getU32(header, 0),
					InetAddress.getByAddress(RecordAccess.getBytes(record, addrOffs, addrSize)),
					new AS(RecordAccess.getU32(record, 0)),
					oldState,
					newState,
                    MRTConstants.UPDATE_STR_BGP4MP,
					header,
					record);

		}

		default:
			break;
		}

		return new MRTRecord(header, record);
	}

	private void parsePeerIndexTable() throws Exception {
		/*
		 * route_btoa.System_err_println("in BGPFileReader.parsePeerIndexTable\nheader:");
		 * RecordAccess.dump(header); route_btoa.System_err_println("record");
		 * RecordAccess.dump(record);
		 */
		// route_btoa.System_err_println("\nin parsePeerIndexTable()...");
		/*
		 * 0 1 2 3 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0
		 * 1 +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
		 * Collector BGP ID |
		 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
		 * View Name Length | View Name (variable) |
		 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+ |
		 * Peer Count | +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
		 */

		int here = 0;
		// long CollectorBGPId = RecordAccess.getU32 (this.record,here);
		here += 4;
		int ViewNameLen = RecordAccess.getU16(this.record, here);
		here += 2;
		// String ViewName = null;
		if (ViewNameLen > 0) {
			// TODO extract ViewName
			here += ViewNameLen;
		}
		int PeerCount = RecordAccess.getU16(this.record, here);
		here += 2;

		/*
		 * route_btoa.System_err_println(String.format("Collector BGP Id: 0x%08X",CollectorBGPId));
		 * route_btoa.System_err_println(String.format("View Name Length = %d",ViewNameLen));
		 * route_btoa.System_err_println(String.format(" has %d peers\n",PeerCount));
		 */
		bgpId = new long[PeerCount];
		peerAS = new org.javamrt.mrt.AS[PeerCount];
		peerIP = new InetAddress[PeerCount];

		for (int i = 0; i < PeerCount; i++) {
			/*
			 * 0 1 2 3 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8
			 * 9 0 1
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * | Peer Type |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * | Peer BGP ID |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * | Peer IP address (variable) |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * | Peer AS (variable) |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 *
			 * The Peer Type field is a bit field which encodes the type of the
			 * AS and IP address as follows:
			 *
			 * Bit 0 - unset for IPv4 Peer IP address, set for IPv6 Bit 1 -
			 * unset when Peer AS field is 16 bits, set when it's 32 bits
			 */
			int peerType = RecordAccess.getU8(this.record, here++);
			bgpId[i] = RecordAccess.getU32(this.record, here);
			here += 4;
			if ((peerType & 0x01) == 0) {
				peerIP[i] = InetAddress.getByAddress(RecordAccess.getBytes(
						this.record, here, 4));
				here += 4;
			} else {
				peerIP[i] = InetAddress.getByAddress(RecordAccess.getBytes(
						this.record, here, 16));
				here += 16;
			}
			if ((peerType & 0x02) == 0) {
				peerAS[i] = new AS(RecordAccess.getU16(this.record, here));
				here += 2;
			} else {
				peerAS[i] = new AS(RecordAccess.getU32(this.record, here));
				here += 4;
			}

			// route_btoa.System_err_println("Peer "+i+"("+bgpId[i]+"): "+peerIP[i].getHostAddress()+" "+peerAS[i]);
		}

		// route_btoa.exit(0);
	}

	private long[] bgpId = null;
	private org.javamrt.mrt.AS peerAS[] = null;
	private java.net.InetAddress peerIP[] = null;

	private void parseTableDumpv2(int NLRItype) throws Exception {

		if (Debug.compileDebug) {
			Debug.printf("parseTableDumpv2(%d)\nheader:", NLRItype);
			Debug.dump(header);
			Debug.println("record:");
			Debug.dump(record);
		}

		int offset = 0;
		long sequenceNo = RecordAccess.getU32(this.record, offset);
		offset = 4;
		Nlri nlri = new Nlri(this.record, offset, NLRItype);
		offset += nlri.getOffset();

		int entryCount = RecordAccess.getU16(this.record, offset);
		offset += 2;

		if (debug) {
			route_btoa.System_err_println("Sequence = " + sequenceNo);
			route_btoa.System_err_println("NLRI     = " + nlri.toPrefix().toString()
                    + " [" + nlri.getOffset() + "]");
			route_btoa.System_err_println("entries  = " + entryCount);
		}

		for (int i = 0; i < entryCount; i++) {
			int peerIndex = RecordAccess.getU16(this.record, offset);

			if (debug) {
				route_btoa.System_err_println(String.format("peerIndex = %d; peer = %s(%s)\n",
                        peerIndex, MRTConstants.ipAddressString(peerIP[peerIndex], false), peerAS[peerIndex].toString("AS")));
			}

			offset += 2;
			// TODO use origTime if appropriate
			//
			// long timeOrig=RecordAccess.getU32(this.record,offset);
			offset += 4;
			int attrLen = RecordAccess.getU16(this.record, offset);
			offset += 2;
			Attributes attributes = new Attributes(record, attrLen, offset,4);
			offset += attrLen;


			recordFifo.add(new TableDumpv2(
					1, // int view,
					(int) (sequenceNo & 0xffff),
					nlri,
					time,
					peerIP[peerIndex], // InetAddr
					peerAS[peerIndex],
					attributes,
					header,
					record));
		}
	}

    //Process MP_REACH and MP_UNREACH
	private void processReachAndUnreach(Attributes attributes, InetAddress srcIP, AS srcAs, String updateStrType) throws Exception{
        MpUnReach mpUnreach = (MpUnReach) attributes.getAttribute(MRTConstants.ATTRIBUTE_MP_UNREACH);

        if (mpUnreach != null) {
            for (Nlri mpu : mpUnreach.getNlri()) {
                recordFifo.add(new Withdraw(header, record, srcIP, srcAs, mpu.toPrefix(), updateStrType));
            }
        }

        MpReach mpReach = (MpReach) attributes.getAttribute(MRTConstants.ATTRIBUTE_MP_REACH);

        if (mpReach != null) {
            if (Debug.compileDebug) Debug.printf("Has MP_REACH (%s)\n",mpReach.getNlri());
            for (Nlri mpu : mpReach.getNlri()) {
                recordFifo.add(new Advertisement(header, record, srcIP, srcAs, mpu
                        .toPrefix(), attributes, updateStrType));
            }
        }

    }

	private MRTRecord parseBgp4Update(int asSize) throws Exception {
		// Bgp4Update update;

		// TODO reconocer los AS de 4 bytes aquí

		int offset = 0;
		AS srcAs = new AS(RecordAccess.getUINT(record, offset, asSize)); offset = asSize;
		AS dstAs = new AS(RecordAccess.getUINT(record, offset, asSize)); offset +=asSize;
		int iface = RecordAccess.getU16(record, offset); offset += 2;
		int afi = RecordAccess.getU16(record, offset); offset += 2;
//		int offset = 2 * asSize + 4;
		int addrSize = (afi == MRTConstants.AFI_IPv6) ? 16 : 4;

		InetAddress srcIP = InetAddress.getByAddress(RecordAccess.getBytes(
				record, offset, addrSize));
		offset += addrSize;
		InetAddress dstIP = InetAddress.getByAddress(RecordAccess.getBytes(
				record, offset, addrSize));
		offset += addrSize;

		/*
		 * skip the following 16 bytes which are the signature of the BGP header
		 */
		offset += 16;

		int bgpSize = RecordAccess.getU16(record, offset); offset += 2;
		int bgpType = RecordAccess.getU8(record, offset); offset ++;

		if (Debug.compileDebug) {
			Debug.printf("Bgp4Update(asSize = %d)\n", asSize);
			Debug.printf("AS srcAs     = %s\n", srcAs.toString());
			Debug.printf("AS dstAs     = %s\n", dstAs.toString());
			Debug.printf("int iface    = %d\n", iface);
			Debug.printf("int Afi      = %d\n", afi);
			Debug.printf("int addrSize = %d\n", addrSize);
			Debug.println("srcIP        = " + srcIP.getHostAddress());
			Debug.println("dstIP        = " + dstIP.getHostAddress());
			Debug.printf("bgpSize      = %d\n", bgpSize);
			Debug.println("bgpType      = " + MRTConstants.bgpType(bgpType));
			Debug.dump(record);
		}
		switch (bgpType) {
		case MRTConstants.BGP4MSG_KEEPALIVE:
			return new KeepAlive(header, record);

		case MRTConstants.BGP4MSG_OPEN:
			int offsetForOpen = offset+5;
			long bgpId = RecordAccess.getU32(record, offsetForOpen);
			return new Open(header, record, srcIP, srcAs, bgpId);

		case MRTConstants.BGP4MSG_NOTIFICATION:
			return new Notification(header, record, srcIP, srcAs);

		case MRTConstants.BGP4MSG_UPDATE:
			break; // to continue after case()

		case MRTConstants.BGP4MSG_REFRESH:
			return new Refresh(header, record);

		default:
			throw new Exception("Unknown BGP4 record type(" + bgpType + ")");
		}

		/*
		 * Here is where the update starts
		 */

		int unfeasibleLen = RecordAccess.getU16(record, offset);
		offset += 2;
		if (Debug.compileDebug) Debug.printf("int unfeasibleLen = %d\n",unfeasibleLen);


		for (int i = 0; i < unfeasibleLen;) {
			//The withdraws out of the Attributs are going to be always ipv4
			Nlri wNlri = new Nlri(record, offset, 1);
			offset += wNlri.getOffset();
			i += wNlri.getOffset();

			recordFifo
					.add(new Withdraw(header, record, srcIP, srcAs, wNlri.toPrefix(),MRTConstants.UPDATE_STR_BGP4MP));
		}

		int attrLen = RecordAccess.getU16(record, offset);
		if (Debug.compileDebug) Debug.printf("attrLen = %d, offset =%d (%d)\n",attrLen,offset,offset+attrLen+2);
		offset += 2;

		if (attrLen > 0) {
			Attributes attributes = null;
			try {
				attributes = new Attributes(record, attrLen, offset,asSize);
			} catch (RFC4893Exception rfce) {
				//
				// piggyback peer and time info
				//
				rfce.setTimestamp(this.time);
				rfce.setPeer(srcIP);
				rfce.setAS(srcAs);
				throw rfce;
			} catch (Exception e) {
				throw e;
			}

			// Process MP_REACH and MP_UNREACH
            processReachAndUnreach(attributes, srcIP, srcAs, MRTConstants.UPDATE_STR_BGP4MP);


//			MpUnReach mpUnreach = (MpUnReach) attributes
//					.getAttribute(MRTConstants.ATTRIBUTE_MP_UNREACH);
//
//			if (mpUnreach != null) {
//				for (Nlri mpu : mpUnreach.getNlri()) {
//					recordFifo.add(new Withdraw(header, record, srcIP, srcAs, mpu.toPrefix(), MRTConstants.UPDATE_STR_BGP4MP));
//				}
//			}
//
//			MpReach mpReach = (MpReach) attributes
//					.getAttribute(MRTConstants.ATTRIBUTE_MP_REACH);
//
//			if (mpReach != null) {
//				if (Debug.compileDebug) Debug.printf("Has MP_REACH (%s)\n",mpReach.getNlri());
//				for (Nlri mpu : mpReach.getNlri()) {
//					recordFifo.add(new Advertisement(header, record, srcIP, srcAs, mpu
//							.toPrefix(), attributes, MRTConstants.UPDATE_STR_BGP4MP));
//				}
//			}

			/*
			 * if (mpReach != null || mpUnreach != null)
			 * route_btoa.System_err_println("This is the whole record");
			 * RecordAccess.dump(record);
			 * route_btoa.System_err_println("These are the attributes");
			 * RecordAccess.dump(record,offset,attrLen);
			 * route_btoa.System_err_println("int attrLen = "+attrLen);
			 *
			 * throw new Exception("MP_REACH attribute!");
			 */

			offset += attrLen;

			if (Debug.compileDebug) Debug.debug("offset(%d) record.length (%d)\n",offset,record.length);
			while (offset < record.length) {
				//The announcements out of the Attributs are going to be always ipv4
				Nlri aNlri = new Nlri(record, offset, 1);
				offset += aNlri.getOffset();

				recordFifo.add(new Advertisement(header, record, srcIP, srcAs,
												 aNlri.toPrefix(), attributes, MRTConstants.UPDATE_STR_BGP4MP));
			}
		}
		if (recordFifo.isEmpty()) {
			if (Debug.compileDebug)
				if (Debug.doDebug)
					throw new BGPFileReaderException("recordFifo empty!", header);
			return null;
		}
		return recordFifo.remove();
	}

	private MRTRecord parseBgp4Entry(int AFI) throws Exception {
		/*
		 * TODO: this doesn't work as expected yet
		 */
		if (Debug.compileDebug) {
			Debug.debug("in parseBgp4Entry\n");
			Debug.dump(record);
		}
		int addrSize = (AFI == MRTConstants.AFI_IPv4) ? 4 : 16;

		int view = RecordAccess.getU16(record, 0);
		int status = RecordAccess.getU16(record, 2);
		long rtime = RecordAccess.getU32(record, 4);
		int af = RecordAccess.getU16(record, 8);
		int safi = RecordAccess.getU8(record, 10);
		int nhl = RecordAccess.getU8(record, 11);

		if (Debug.compileDebug) {
			Debug.debug("int  view   = %d\n",view);
			Debug.debug("int  status = %d\n",status);
			Debug.debug("long rtime  = %d\n",rtime);
			Debug.debug("int  af     = %d\n",af);
			Debug.debug("int  safi   = %d\n",safi);
			Debug.debug("int  nhl    = %d\n",nhl);
		}
		int offset = 12;
		InetAddress nextHop = InetAddress.getByAddress(RecordAccess.getBytes(
				record, offset, addrSize));
		offset += addrSize;
		Nlri prefix = new Nlri(record, offset, AFI);
		offset += prefix.getOffset();

		Attributes attrs = new Attributes(record, record.length - offset,
				offset);
		ASPath aspath = attrs.getASPath();

		AS neighborAS = null;

		if (aspath != null)
			neighborAS = aspath.get(0);

		return new TableDumpv2(view, 1, prefix, rtime, nextHop, neighborAS,
				attrs, header, record);
	}

	private void parseGenericRib() throws BGPFileReaderException{
		// TODO: implement
		throw new BGPFileReaderException("TODO : parseGenericRib",new byte[1]);
	}

	private void parseTableDumpv2Multicast() throws BGPFileReaderException {
		// TODO: implement
		throw new BGPFileReaderException("TODO: parseTableDumpv2Multicast()",new byte[1]);
	}

	public boolean eof() {
		return this.eof;
	}

}
