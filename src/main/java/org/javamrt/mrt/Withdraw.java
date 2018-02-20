// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import java.net.InetAddress;

public class Withdraw extends Bgp4Update
{
	public Withdraw(byte[] header,
                    byte[] record,
					InetAddress peerIP,
                    AS peerAS,
                    Prefix prefix,
					String updateStr)
	{
		super(header, record, peerIP, peerAS, prefix, updateStr);
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
