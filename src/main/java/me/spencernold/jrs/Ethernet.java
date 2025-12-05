package me.spencernold.jrs;

public class Ethernet extends Protocol {

    public static final int HEADER_SIZE = 14;

    private byte[] src;
    private byte[] dst;
    private short type;

    public Ethernet(byte[] src, byte[] dst, short type) {
        this.src = src;
        this.dst = dst;
        this.type = type;
    }

    public Ethernet(String src, String dst, short type) {
        this(MediumAccessControl.encode(src), MediumAccessControl.encode(dst), type);
    }

    @Override
    public void write(NetworkByteBuf buf) {
        buf.write(dst);
        buf.write(src);
        buf.writeShort(type);
    }

    @Override
    public void read(NetworkByteBuf buf) {
        dst = buf.read(6);
        src = buf.read(6);
        type = buf.readShort();
    }

    public boolean is(byte[] src, byte[] dst, Short type) {
        if (src != null && !NetworkByteBuf.compare(this.src, 0, src, 0, 6))
            return false;
        if (dst != null && !NetworkByteBuf.compare(this.dst, 0, dst, 0, 6))
            return false;
        return type == null || this.type == type;
    }

    public byte[] getSourceMac() {
        return src;
    }

    public byte[] getDestinationMac() {
        return dst;
    }

    public short getType() {
        return type;
    }

    @Override
    public int getHeaderSize() {
        return HEADER_SIZE;
    }
}
