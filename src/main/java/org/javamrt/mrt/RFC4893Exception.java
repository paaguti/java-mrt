// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import java.net.InetAddress;

public class RFC4893Exception extends Exception {

	private static final long serialVersionUID = 3786016760039113637L;
	private int cause;
	private long timestamp;
	private InetAddress peer;
	private AS as;
	private ASPath oldASPath;
	private ASPath as4path;

	public RFC4893Exception(int cause,
				long timestamp,
				InetAddress peer,
				AS as,
				ASPath oldASPath,
				ASPath as4path) {
		super();
		this.cause = cause;
		this.timestamp = timestamp;
		this.peer      = peer;
		this.as        = as;
		this.oldASPath = oldASPath;
		this.as4path   = as4path;
	}

	public RFC4893Exception(int cause,ASPath oldASPath, ASPath as4path) {
		super();
		this.cause = cause;
		this.timestamp = 0;
		this.peer      = null;
		this.oldASPath = oldASPath;
		this.as4path   = as4path;
	}

	public void setTimestamp(long timestamp) {
		this.timestamp = timestamp;
	}

	public long getTimestamp() {
		return this.timestamp;
	}

	public InetAddress getPeer() {
		return peer;
	}

	public void setPeer(InetAddress peer) {
		this.peer = peer;
	}

	public void setAS(AS srcAs) {
		this.as = srcAs;
	}

	public AS getAS() {
		return this.as;
	}

	public void setOldASPath(ASPath oldASPath) {
		this.oldASPath = oldASPath;
	}
	public ASPath getOldASPath() {
		return this.oldASPath;
	}

	public void setAS4Path(ASPath as4path) {
		this.as4path = as4path;
	}
	public ASPath getAS4Path() {
		return this.as4path;
	}

	public String toString() {
		if (peer == null || as == null)
			return String.format("RFC4893 violation @ %d: AS4PATH contains %s",this.timestamp,MRTConstants.asPathString(cause));
		return String.format(
				"RFC4893 violation @ %d from (%s;%s) with "+
				"AS4PATH containing %s\n"+
				" while trying to modify: %s\n"+
				" with 4 byte ASPATH:     %s\n",
					this.timestamp,
					this.as.toString("AS"),
					this.peer.getHostAddress(),
					MRTConstants.asPathString(cause),
					this.oldASPath.toString(),
					this.as4path.toString());
	}
}
