package org.javamrt.mrt;

import java.net.InetAddress;

public class Withdraw extends Bgp4Update 
{
	public Withdraw(byte[] header, 
					InetAddress peerIP, 
					AS peerAS, 
					Prefix prefix) 
	{
		super(header, peerIP, peerAS, prefix);
		this.updateType = 'W';
	}

	@Override
	public boolean isIPv4() {
		return this.prefix.isIPv4();
	}

	@Override
	public boolean isIPv6() {
		return this.prefix.isIPv6();
	}

	/**
	 * overrides {@link Bgp4Update#toWithdraw()}
	 */
	@Override
	public Withdraw toWithdraw() {
		return this;
	}

}
