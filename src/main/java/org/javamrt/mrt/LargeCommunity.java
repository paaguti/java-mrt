// This file is part of java-mrt
// A library to parse MRT files

// This file is released under LGPL 3.0
// http://www.gnu.org/licenses/lgpl-3.0-standalone.html

package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;

public class LargeCommunity implements Attribute {
    protected long globalAdministrator;
    protected long localData1;
    protected long localData2;

    LargeCommunity(byte[] buffer) {
        globalAdministrator = RecordAccess.getU32(buffer, 0);
        localData1          = RecordAccess.getU32(buffer, 4);
        localData2          = RecordAccess.getU32(buffer, 8);
    }

    public String toString() {
        return String.format("%d:%d:%d", globalAdministrator,
                                         localData1,
                                         localData2);
    }

    public boolean equals(Object o) {
        if (o == null)
            return false;
        if (this == o)
            return true;
        if (o.getClass() == this.getClass()) {
            LargeCommunity lo = (LargeCommunity) o;
            return (lo.globalAdministrator == this.globalAdministrator
                 && lo.localData1 == this.localData1
                 && lo.localData2 == this.localData1);
        }
        return false;
    }

}
