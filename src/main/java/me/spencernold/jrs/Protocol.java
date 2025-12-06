package me.spencernold.jrs;

public abstract class Protocol {

    protected NetworkStack stack;

    protected int start = 0, end = 0;

    public byte[] getFrame(NetworkByteBuf buf) {
        return buf.internal(start, end);
    }

    public int getBufferStartPos() {
        return start;
    }

    public int getBufferEndPos() {
        return end;
    }

    public int getHeaderSize() {
        return end - start;
    }

    public abstract int getExpectedHeaderSize();

    public abstract void write(NetworkByteBuf buf);
    public abstract void read(NetworkByteBuf buf);
}
