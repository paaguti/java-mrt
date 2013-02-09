package org.javamrt.mrt;

import java.net.InetAddress;

import org.javamrt.utils.InetAddressComparator;

public class peer implements Comparable<peer>{
	public AS as;
	public InetAddress ip;
	public peer(AS as,InetAddress ip) {
		this.as = as;
		this.ip = ip;
	}
	public peer(InetAddress ip,AS as) {
		this.as = as;
		this.ip = ip;
	}
	@Override
	public int compareTo(peer o) {
		int result = this.as.compareTo(o.as);
		if (result == 0)
			result = InetAddressComparator.compara(this.ip, o.ip);
		return 0;
	}
	public boolean equals(Object o) {
		if (this == o) return true;
		if (o == null) return false;
		if (o instanceof peer) {
			return this.as.equals(((peer)o).as) &&
					this.ip.equals(((peer)o).ip);
		}
		return false;
	}
}
