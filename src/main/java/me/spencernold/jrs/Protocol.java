package me.spencernold.jrs;

public abstract class Protocol {

    public abstract void write(NetworkByteBuf buf);
    public abstract void read(NetworkByteBuf buf);
    public abstract int getHeaderSize();
}
