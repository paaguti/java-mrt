package org.javamrt.mrt;

import org.javamrt.utils.RecordAccess;

import java.util.Arrays;

public class UnsupportedAttribute implements Attribute {

    private final int type;
    private final byte[] buffer;

    public UnsupportedAttribute(int type, byte[] buffer) {
        this.type = type;
        this.buffer = buffer;
    }

    @Override
    public String toString() {
        return "Attribute type " + type + ": " + RecordAccess.arrayToString(buffer);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        UnsupportedAttribute that = (UnsupportedAttribute) o;

        if (type != that.type) return false;
        return Arrays.equals(buffer, that.buffer);
    }

    @Override
    public int hashCode() {
        int result = type;
        result = 31 * result + Arrays.hashCode(buffer);
        return result;
    }
}
