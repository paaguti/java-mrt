// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.progs.route_btoa;
import org.javamrt.utils.Debug;
import org.javamrt.utils.RecordAccess;

import java.io.BufferedInputStream;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.zip.GZIPInputStream;

public class BGPFileReader implements Closeable {
	private static final boolean debug = false;
	private static boolean lenient = false;

	private InputStream in;
	private LinkedList<MRTRecord> recordFifo;

	private boolean eof;
	public static final byte[] BGP_MARKER = new byte[]{-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
	private static final byte[] UNKNOWN_IPV4 = new byte[]{0, 0, 0, 0};
	private static final byte[] UNKNOWN_IPV6 = new byte[]{0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0};

	private String streamName;
	/*****
	 *
	 * public BGPFileReader (BufferedInputStream in)
	 *
	 * create a new BGPFileReader from BufferedInputStream <in>
	 */

	public BGPFileReader(InputStream in) {
		this.in = in;
		this.streamName = in.toString();
		this.recordFifo = new LinkedList<MRTRecord>();
		this.eof = false;
	}

	/*****
	 *
	 * public BGPFileReader (String name)
	 *
	 * create a new BGPFileReader from BufferedInputStream specified by the
	 * String name
	 * @throws IOException
	 */

	public BGPFileReader(String name) throws IOException {
		InputStream inStream = null;
		this.streamName = name;

		try { // to open the name as a URL
			java.net.URL url = new java.net.URL(name);
			inStream = url.openStream();
		} catch (java.net.MalformedURLException e) {
			File inFile = new File(name);
			if (!inFile.exists())
				throw new java.io.FileNotFoundException(name);
			inStream = new FileInputStream(inFile);
		}


		if (this.streamName.endsWith(".gz")) {
			this.in = new GZIPInputStream(inStream, 8192);
		} else {
			this.in = (inStream instanceof BufferedInputStream || inStream instanceof GZIPInputStream)
					  ? inStream : new BufferedInputStream(inStream);
		}

		this.recordFifo = new LinkedList<MRTRecord>();
		this.eof = false;
	}

	public BGPFileReader(File f) throws IOException {
		if (!f.exists())
			throw new java.io.FileNotFoundException(f.toString());
		FileInputStream inStream = new FileInputStream(f);
		this.streamName = f.getCanonicalPath();
		if (this.streamName.endsWith(".gz")) {
			this.in = new GZIPInputStream(inStream, 8192);
		} else {
			this.in = new BufferedInputStream(inStream);
		}
		this.recordFifo = new LinkedList<MRTRecord>();
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
	}

	/**
	 * toString(): return the name of the input Stream
	 */
	public String toString() {
		return this.streamName;
	}

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
			 * if the queue is empty, read from the file
			 */

			final byte[] header = new byte[12];
			byte[] record;
			int bytesRead;
			try {
				bytesRead = readFromInputStream(this.in, header, header.length);
			} catch (IOException e) {
				throw new MalformedBgpStreamException(e);
			}
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
			if (bytesRead != header.length) {
				this.eof = true;
				throw new MalformedBgpStreamException(
						String.format("Truncated file: %d instead of %d bytes; header: %s",
						bytesRead,
						header.length, RecordAccess.arrayToString(header)));
			}
			recordCounter ++;
			if (Debug.compileDebug)
				RecordAccess.dump(Debug.debugStream, header);
			time = RecordAccess.getU32(header, 0);
			final int type = RecordAccess.getU16(header, 4);
			final int subtype = RecordAccess.getU16(header, 6);

			final long recordlen = RecordAccess.getU32(header, 8);

			if (Debug.compileDebug) Debug.println("TIME: " + time + "\n TYPE: " + type
						+ "\n SUBTYPE: " + subtype + "\n RECORDLENGTH: "
						+ recordlen);

			if (recordlen > Integer.MAX_VALUE) {
				throw new MalformedBgpStreamException("Can't have a record longer than 2147483647 bytes");
			}

			try {
				record = new byte[(int) recordlen];
				bytesRead = readFromInputStream(this.in, record, record.length);
			} catch (OutOfMemoryError e) {
				throw new BGPFileReaderException(
						"Got OutOfMemoryError while trying to read next record (" + recordlen + " bytes) into memory", header);
			} catch (IOException e ) {
				throw new MalformedBgpStreamException(e);
			}

			if (bytesRead != record.length) {
				final String message = String.format("Truncated file: %,d instead of %,d bytes: %s",
						bytesRead, record.length, RecordAccess.arrayToString(header));
				if (lenient) {
					route_btoa.System_err_println(message + "; trying to parse anyways");
					record = Arrays.copyOf(record, bytesRead);
				} else {
					this.eof = true;
					throw new MalformedBgpStreamException(message);
				}
			}

			if (Debug.compileDebug)
				Debug.dump(record);
			/*
			 * Record parsing
			 */
			switch (type) {
				case MRTConstants.TABLE_DUMP:
					return parseTableDump(header, record, subtype);

				case MRTConstants.TABLE_DUMP_v2:

					switch (subtype) {
						case MRTConstants.PEER_INDEX_TABLE:
							parsePeerIndexTable(record);
							break;
						case 2:
							parseTableDumpv2(header, record, MRTConstants.AFI_IPv4);
							break;
						case 4:
							parseTableDumpv2(header, record, MRTConstants.AFI_IPv6);
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
					MRTRecord bgp4mp = parseBgp4mp(header, record, subtype);
					if ((bgp4mp) != null) {
						return bgp4mp;
					}
					break;

                case MRTConstants.BGPDUMP_TYPE_MRTD_BGP:
                    MRTRecord bgpRecord = parseBgp(header, record, subtype);
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
	private static int readFromInputStream(InputStream bis, byte[] output, int length) throws IOException {
		int remaining = length;
		int read;

		while (((read = bis.read(output, length - remaining, remaining)) > 0) && ((remaining -= read) > 0));

		return ((read == -1 && remaining == length) ? -1 : length - remaining);
	}

	private MRTRecord parseTableDump(byte[] header, byte[] record, int subtype) throws Exception {
		switch (subtype) {
		case MRTConstants.AFI_IPv4:
		case MRTConstants.AFI_IPv6:
			return new TableDump(header, record, subtype);
		default:
			throw new BGPFileReaderException("Unknown TABLE_DUMP subtype"
					+ subtype, header);
		}

	}

	private MRTRecord parseBgp(byte[] header, byte[] record, int subtype) throws Exception {
        //Based on https://tools.ietf.org/html/rfc1771
		//         https://tools.ietf.org/html/rfc6396
        switch (subtype) {
			case MRTConstants.BGPDUMP_SUBTYPE_MRTD_BGP_NULL:
			case MRTConstants.BGPDUMP_SUBTYPE_MRTD_BGP_PREF_UPDATE:
			case MRTConstants.BGPDUMP_SUBTYPE_MRTD_BGP_SYNC:
				return new MRTRecord(header, record);

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
		}

		if (subtype >= MRTConstants.BGPDUMP_SUBTYPE_MRTD_BGP_UPDATE &&
				subtype <= MRTConstants.BGPDUMP_SUBTYPE_MRTD_BGP_KEEPALIVE) {

        	if (record.length < 12) {
        		throw new BGPFileReaderException(
        				"Not enough bytes to read common BGP fields (at least 12 are needed): ", record);
			}
			final AS peerAS = new AS(Arrays.copyOfRange(record, 0, 2));
			final InetAddress peerIP = InetAddress.getByAddress(Arrays.copyOfRange(record, 2, 6));
			final AS localAS = new AS(Arrays.copyOfRange(record, 6, 8));
			final InetAddress localIP = InetAddress.getByAddress(Arrays.copyOfRange(record, 8, 12));

			switch (subtype) {
				case MRTConstants.BGPDUMP_SUBTYPE_MRTD_BGP_UPDATE:
					return getBgpUpdate(header, record, peerAS, peerIP);

				case MRTConstants.BGPDUMP_SUBTYPE_MRTD_BGP_OPEN:
					final long bgpId = RecordAccess.getUINT(record, 12 + 5, 4);
					return new Open(header, record, peerIP, peerAS, bgpId);

				case MRTConstants.BGPDUMP_SUBTYPE_MRTD_BGP_NOTIFY:
					return new Notification(header, record, peerIP, peerAS);

				case MRTConstants.BGPDUMP_SUBTYPE_MRTD_BGP_KEEPALIVE:
					return new KeepAlive(header, record, peerAS, peerIP, localAS, localIP);
			}
		}

		return new MRTRecord(header, record);
    }

    private MRTRecord getBgpUpdate(byte[] header, byte[] record, AS peerAS, InetAddress peerIP) throws Exception{
        int offset = 12;
        final int asSize = 2;

        long wSize = RecordAccess.getUINT(record, offset, 2);
        offset += 2;

        // WITHDRAWS
        if(wSize > 0) { // Withdraw if the size > than 0.
            for (int i = 0; i < wSize;) {
            	Prefix prefix = lenient ? new LenientPrefix(record, offset, 1) : new Nlri(record, offset, 1);
                offset += prefix.getOffset();
                i += prefix.getOffset();

				Withdraw withdraw = new Withdraw(header, record, peerIP, peerAS, prefix, MRTConstants.UPDATE_STR_BGP);
                recordFifo.add(withdraw);
            }

            //Reading length of attributes
            int attrLen = RecordAccess.getU16(record, offset);
            offset +=2;

            if(attrLen > 0){

                Attributes attributes;
                try {
                    attributes = new Attributes(record, attrLen, offset,asSize);
                } catch (RFC4893Exception rfce) {
                    //
                    // piggyback peer and time info
                    //
                    rfce.setTimestamp(this.time);
                    rfce.setPeer(peerIP);
                    rfce.setAS(peerAS);
                    throw rfce;
                }

				//Process MP_REACH and MP_UNREACH
                processReachAndUnreach(header, record, attributes, peerIP, peerAS, MRTConstants.UPDATE_STR_BGP);

                offset += attrLen;
                while (offset < record.length) {
                    Prefix aNlri = lenient ? new LenientPrefix(record, offset, 1) : new Nlri(record, offset, 1);
                    offset += aNlri.getOffset();

                    recordFifo.add(new Advertisement(header, record, peerIP, peerAS,
                            aNlri, attributes, MRTConstants.UPDATE_STR_BGP));
                }
            }

        return recordFifo.remove();
    }

	private MRTRecord parseBgp4mp(byte[] header, byte[] record, int subtype) throws Exception {
		// route_btoa.System_err_println("parseBgp4mp("+MRTConstants.mpSubType(subtype)+")");
		switch (subtype) {
		case MRTConstants.BGP4MP_MESSAGE:
		case MRTConstants.BGP4MP_MESSAGE_AS4:
			return parseBgp4mpMessage(header, record, (subtype == MRTConstants.BGP4MP_MESSAGE) ? 2 : 4);

			/*
			 * TODO
			 * TTOODDOO::::
			 *
			 *
			 * case BGP4MP_SNAPSHOT: return new Bgp4mpSnapshot(header,record);
			 */

		case MRTConstants.BGP4MP_ENTRY:
			return parseBgp4Entry(header, record, RecordAccess.getU16(record, 6));

		case MRTConstants.BGP4MP_STATE_CHANGE: {
			/*
			 *  0                   1                   2                   3
			 *  0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * |         Peer AS Number        |        Local AS Number        |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * |        Interface Index        |        Address Family         |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * |                      Peer IP Address (variable)               |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * |                      Local IP Address (variable)              |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 * |            Old State          |          New State            |
			 * +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
			 */
			if (record.length < 20) {
				if (lenient && record.length == 8) {
					// seems like known zebra/quagga 8-byte state change bug
					route_btoa.System_err_println("8-byte long BGP4MP_STATE_CHANGE; parsing accordingly");
					final AS peerAs = new AS(RecordAccess.getBytes(record, 0, 2));
					final AS localAs = new AS(RecordAccess.getBytes(record, 2, 2));
					int oldState = RecordAccess.getU16(record, 4);
					int newState = RecordAccess.getU16(record, 6);
					return new StateChange(
							RecordAccess.getU32(header, 0),
							unknownAddress(MRTConstants.AFI_IPv4),
							peerAs,
							oldState,
							newState,
		                    MRTConstants.UPDATE_STR_BGP4MP,
							header,
							record);
				} else {
					throw new BGPFileReaderException("BGP4MP state change record smaller than 20 bytes: ", record);
				}
			}
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

	private void parsePeerIndexTable(byte[] record) throws Exception {
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
		int ViewNameLen = RecordAccess.getU16(record, here);
		here += 2;
		// String ViewName = null;
		if (ViewNameLen > 0) {
			// TODO extract ViewName
			here += ViewNameLen;
		}
		int PeerCount = RecordAccess.getU16(record, here);
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
			int peerType = RecordAccess.getU8(record, here++);
			bgpId[i] = RecordAccess.getU32(record, here);
			here += 4;
			if ((peerType & 0x01) == 0) {
				peerIP[i] = InetAddress.getByAddress(RecordAccess.getBytes(
						record, here, 4));
				here += 4;
			} else {
				peerIP[i] = InetAddress.getByAddress(RecordAccess.getBytes(
						record, here, 16));
				here += 16;
			}
			if ((peerType & 0x02) == 0) {
				peerAS[i] = new AS(RecordAccess.getU16(record, here));
				here += 2;
			} else {
				peerAS[i] = new AS(RecordAccess.getU32(record, here));
				here += 4;
			}

			// route_btoa.System_err_println("Peer "+i+"("+bgpId[i]+"): "+peerIP[i].getHostAddress()+" "+peerAS[i]);
		}

		// route_btoa.exit(0);
	}

	private long[] bgpId = null;
	private org.javamrt.mrt.AS peerAS[] = null;
	private java.net.InetAddress peerIP[] = null;

	private void parseTableDumpv2(byte[] header, byte[] record, int nlriType) throws Exception {

		if (Debug.compileDebug) {
			Debug.printf("parseTableDumpv2(%d)\nheader:", nlriType);
			Debug.dump(header);
			Debug.println("record:");
			Debug.dump(record);
		}

		int offset = 0;
		long sequenceNo = RecordAccess.getU32(record, offset);
		offset = 4;
		Prefix nlri = lenient ? new LenientPrefix(record, offset, nlriType) : new Nlri(record, offset, nlriType);
		offset += nlri.getOffset();

		int entryCount = RecordAccess.getU16(record, offset);
		offset += 2;

		if (debug) {
			route_btoa.System_err_println("Sequence = " + sequenceNo);
			route_btoa.System_err_println("NLRI     = " + nlri.toString()
                    + " [" + nlri.getOffset() + "]");
			route_btoa.System_err_println("entries  = " + entryCount);
		}

		for (int i = 0; i < entryCount; i++) {
			int peerIndex = RecordAccess.getU16(record, offset);

			if (debug) {
				route_btoa.System_err_println(String.format("peerIndex = %d; peer = %s(%s)\n",
                        peerIndex, MRTConstants.ipAddressString(peerIP[peerIndex], false), peerAS[peerIndex].toString("AS")));
			}

			offset += 2;
			// TODO use origTime if appropriate
			//
			// long timeOrig=RecordAccess.getU32(this.record,offset);
			offset += 4;
			int attrLen = RecordAccess.getU16(record, offset);
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
	private void processReachAndUnreach(byte[] header, byte[] record, Attributes attributes, InetAddress srcIP, AS srcAs, String updateStrType) throws Exception{
        MpUnReach mpUnreach = (MpUnReach) attributes.getAttribute(MRTConstants.ATTRIBUTE_MP_UNREACH);

        if (mpUnreach != null) {
            for (Nlri mpu : mpUnreach.getNlri()) {
                recordFifo.add(new Withdraw(header, record, srcIP, srcAs, mpu, updateStrType));
            }
        }

        MpReach mpReach = (MpReach) attributes.getAttribute(MRTConstants.ATTRIBUTE_MP_REACH);

        if (mpReach != null) {
            if (Debug.compileDebug) Debug.printf("Has MP_REACH (%s)\n",mpReach.getNlri());
            for (Nlri mpu : mpReach.getNlri()) {
                recordFifo.add(new Advertisement(header, record, srcIP, srcAs, mpu,
                        attributes, updateStrType));
            }
        }

    }

	private MRTRecord parseBgp4mpMessage(byte[] header, byte[] record, int asSize) throws Exception {
	/*
        0                   1                   2                   3
        0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |         Peer AS Number        |        Local AS Number        |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |        Interface Index        |        Address Family         |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                      Peer IP Address (variable)               |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                      Local IP Address (variable)              |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                    BGP Message... (variable)
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
                     Figure 12: BGP4MP_MESSAGE Subtype

        0                   1                   2                   3
        0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1 2 3 4 5 6 7 8 9 0 1
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                         Peer AS Number                        |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                         Local AS Number                       |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |        Interface Index        |        Address Family         |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                      Peer IP Address (variable)               |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                      Local IP Address (variable)              |
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
       |                    BGP Message... (variable)
       +-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+
                   Figure 13: BGP4MP_MESSAGE_AS4 Subtype
	*/

		final int markerPosition = lenient ?
								   findBgpMarker(record, asSize) :
								   record.length;

		final ByteBuffer buffer = ByteBuffer.wrap(record, 0, markerPosition);

		AS srcAs = notLenientOr(buffer.remaining() >= asSize) ? new AS(RecordAccess.getUINT(buffer, asSize)) : new AS(0L);
		AS dstAs = notLenientOr(buffer.remaining() >= asSize) ? new AS(RecordAccess.getUINT(buffer, asSize)) : new AS(0L);
		int iface = notLenientOr(buffer.remaining() >= 2) ? RecordAccess.getU16(buffer) : 0;
		int afi = notLenientOr(buffer.remaining() >= 2) ? RecordAccess.getU16(buffer) : 0;
		if (afi != MRTConstants.AFI_IPv4 && afi != MRTConstants.AFI_IPv6) {
			final String message = String.format("AFI=%d is unknown", afi);
			if (lenient) {
				route_btoa.System_err_println(message + "; assuming IPv4");
			} else {
				throw new BGPFileReaderException(message, record);
			}
		}
		int addrSize = (afi == MRTConstants.AFI_IPv6) ? 16 : 4;

		InetAddress srcIP = notLenientOr(buffer.remaining() >= addrSize) ?
							InetAddress.getByAddress(RecordAccess.getBytes(buffer, addrSize)) :
							unknownAddress(afi);
		InetAddress dstIP = notLenientOr(buffer.remaining() >= addrSize) ?
							InetAddress.getByAddress(RecordAccess.getBytes(buffer, addrSize)) :
							unknownAddress(afi);
		/*
		 * skip the following 16 bytes which are the signature of the BGP header
		 */
		int offset = buffer.position() + BGP_MARKER.length;

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
			return new KeepAlive(header, record, srcAs, srcIP, dstAs, dstIP);

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
			Prefix wNlri = lenient ? new LenientPrefix(record, offset, 1) : new Nlri(record, offset, 1);
			offset += wNlri.getOffset();
			i += wNlri.getOffset();

			recordFifo
					.add(new Withdraw(header, record, srcIP, srcAs, wNlri,MRTConstants.UPDATE_STR_BGP4MP));
		}

		int attrLen = RecordAccess.getU16(record, offset);
		if (Debug.compileDebug) Debug.printf("attrLen = %d, offset =%d (%d)\n",attrLen,offset,offset+attrLen+2);
		offset += 2;

		if (offset + attrLen > record.length) {
			if (lenient) {
				route_btoa.System_err_println("Attribute length goes over the record length, truncating");
				attrLen = record.length - offset;
			}
		}

		Attributes attributes = null;
		if (attrLen > 0) {
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
			}

			// Process MP_REACH and MP_UNREACH
            processReachAndUnreach(header, record, attributes, srcIP, srcAs, MRTConstants.UPDATE_STR_BGP4MP);


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
				Prefix aNlri = lenient ? new LenientPrefix(record, offset, 1) : new Nlri(record, offset, 1);
				offset += aNlri.getOffset();

				recordFifo.add(new Advertisement(header, record, srcIP, srcAs,
												 aNlri, attributes, MRTConstants.UPDATE_STR_BGP4MP));
			}
		}
		if (recordFifo.isEmpty()) {
			if (Debug.compileDebug)
				if (Debug.doDebug)
					throw new BGPFileReaderException("recordFifo empty!", header);
			return new Bgp4Update(header, record, srcIP, srcAs, null, attributes, "");
		}
		return recordFifo.remove();
	}

	private InetAddress unknownAddress(int afi) {
		try {
			if (afi == MRTConstants.AFI_IPv6) return InetAddress.getByAddress(UNKNOWN_IPV6);
			else return InetAddress.getByAddress(UNKNOWN_IPV4);
		} catch (UnknownHostException e) {
			// should not happen
			throw new RuntimeException(e);
		}
	}

	private int findBgpMarker(byte[] record, int asSize) throws BGPFileReaderException {
		final int endOfSearch = record.length - BGP_MARKER.length;
		if (endOfSearch < 0) {
			throw new BGPFileReaderException("BGP4MP message is too small: ", record);
		}
		final int expectedPositionIpv4 = 2 * asSize + 2 + 2 + 2 * 4;
		final int expectedPositionIpv6 = 2 * asSize + 2 + 2 + 2 * 16;

		if (expectedPositionIpv4 <= endOfSearch && isBgpMarkerAt(record, expectedPositionIpv4)) {
			return expectedPositionIpv4;
		} else if (expectedPositionIpv6 <= endOfSearch && isBgpMarkerAt(record, expectedPositionIpv6)) {
			return expectedPositionIpv6;
		} else {
			int i = 0;
			while (i < endOfSearch) {
				if (isBgpMarkerAt(record, i)) {
					route_btoa.System_err_println(String.format("BGP marker found at offset %d; record re-aligned%n", i));
					return i;
				} else {
					i++;
				}
			}
		}
		throw new BGPFileReaderException("BGP4MP message without BGP marker: ", record);
	}

	private boolean isBgpMarkerAt(byte[] bytes, int offset) {
		return bytes[offset] == BGP_MARKER[0]
				&& Arrays.equals(Arrays.copyOfRange(bytes, offset, offset + BGP_MARKER.length), BGP_MARKER);
	}

	private MRTRecord parseBgp4Entry(byte[] header, byte[] record, int AFI) throws Exception {
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
		Prefix prefix = lenient ? new LenientPrefix(record, offset, AFI) : new Nlri(record, offset, AFI);
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

	public static void setLenient(boolean isLenient) {
		lenient = isLenient;
	}

	public static boolean isLenient() {
		return lenient;
	}

	public static boolean notLenientOr(boolean condition) {
		return !lenient || condition;
	}

}
