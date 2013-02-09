package org.javamrt.mrt;

import java.net.InetAddress;

import org.javamrt.utils.RecordAccess;


public class ClusterList implements Attribute {
	public ClusterList(byte[] buffer) throws Exception {
		clusterList = null;
		if (buffer.length > 0) {
			//
			// FIXME: could this also be Inet6Address ??
			//
			clusterList = new InetAddress[buffer.length / 4];
			int offset = 0;
			while (offset < buffer.length) {
				clusterList[offset / 4] = InetAddress.getByAddress(RecordAccess
						.getBytes(buffer, offset, 4));
				offset += 4;
			}
		}
	}

	protected InetAddress[] clusterList;

	public boolean equals(ClusterList other) {
		if (this.clusterList.length != other.clusterList.length)
			return false;
		for (int i = 0; i < clusterList.length; i++)
			if (!this.clusterList[i].equals(other.clusterList[i]))
				return false;
		return true;
	}

	public boolean equals(Object o) {
		if (o == null)
			return false;
		if (this == o)
			return true;
		if (o instanceof ClusterList)
			return equals((ClusterList) o);
		return false;
	}

	public String toString() {
		StringBuffer result = new StringBuffer();

		for (int i = 0; i < clusterList.length; i++)
			result.append(clusterList[i].getHostAddress() + " ");

		return result.toString();
	}
}
