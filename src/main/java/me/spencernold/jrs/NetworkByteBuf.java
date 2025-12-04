package me.spencernold.jrs;

import java.util.Arrays;

class NetworkByteBuf {

    private final byte[] internal;
    private int index;
    private boolean state; // true == reading, false == writing

    public NetworkByteBuf(int size, boolean reading) {
        this(new byte[size], reading);
    }

    public NetworkByteBuf(byte[] bytes, boolean reading) {
        this.internal = bytes;
        this.index = 0;
        this.state = reading;
    }

    public void write(byte[] bytes) {
        if ((index + bytes.length) > internal.length)
            throw new IllegalStateException("internal buffer overflow; size of the buffer is not big enough");
        System.arraycopy(bytes, 0, internal, index, bytes.length);
        index += bytes.length;
    }

    public byte[] read(int n) {
        if (n > (internal.length - index))
            throw new IllegalStateException("internal buffer underflow; not enough data left in the buffer");
        return Arrays.copyOfRange(internal, index, index += n);
    }

    public void seek(int index) {
        this.index = clamp(index, 0, internal.length);
    }

    public int getWriterPosition() {
        return index;
    }

    public void flip() {
        state = !state;
        index = 0;
    }

    private int clamp(int i, int min, int max) {
        if (i > max)
            return max;
        if (i < min)
            return min;
        return i;
    }
}
