// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.utils;

import java.net.InetAddress;
import java.util.Comparator;

public class InetAddressComparator
implements Comparator<InetAddress>
{
	public static int compara(InetAddress ip1, InetAddress ip2) {
		byte[] a1 = ip1.getAddress();
		byte[] a2 = ip2.getAddress();

		if (a1.length != a2.length)
			return a1.length > a2.length ? 1 : -1;

		for (int i=0;i<a1.length;i++)
			if (a1[i] != a2[i])
				return ((a1[i] & 0x00ff) > (a2[i] & 0x00ff)) ? 1 : -1;
		return 0;
	}

	public int compare(InetAddress ia1, InetAddress ia2) {
		return compara(ia1,ia2);
	}
}
