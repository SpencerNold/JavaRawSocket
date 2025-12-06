package me.spencernold.jrs;

public class InternetProtocol extends Protocol {

    public static final int MIN_HEADER_SIZE = 20;

    private byte versionHeaderLength;
    private byte typeOfService;
    private short totalLength;
    private short identification;
    private short fragmentOffset;
    private byte timeToLive;
    private byte protocol;
    private short checksum;
    private int source;
    private int destination;
    private int[] options;

    public InternetProtocol(byte versionHeaderLength, byte typeOfService, short totalLength, short identification, short fragmentOffset, byte timeToLive, byte protocol, short checksum, int source, int destination, int[] options) {
        this.versionHeaderLength = versionHeaderLength;
        this.typeOfService = typeOfService;
        this.totalLength = totalLength;
        this.identification = identification;
        this.fragmentOffset = fragmentOffset;
        this.timeToLive = timeToLive;
        this.protocol = protocol;
        this.checksum = checksum;
        this.source = source;
        this.destination = destination;
        this.options = options;
    }

    @Override
    public void write(NetworkByteBuf buf) {
        start = buf.getCursor();
        buf.writeByte(versionHeaderLength);
        buf.writeByte(typeOfService);
        buf.writeShort(totalLength);
        buf.writeShort(identification);
        buf.writeShort(fragmentOffset);
        buf.writeByte(timeToLive);
        buf.writeByte(protocol);
        buf.writeShort(checksum);
        buf.writeInt(source);
        buf.writeInt(destination);
        for (int i : options)
            buf.writeInt(i);
        end = buf.getCursor();
        byte[] bytes = buf.internal(start, end);
        buf.seek(start + 10);
        buf.writeShort(ChecksumBinding.calculateIPv4Checksum(bytes, 0, getHeaderSize()));
        buf.seek(end);
    }

    @Override
    public void read(NetworkByteBuf buf) {
        start = buf.getCursor();
        versionHeaderLength = buf.readByte();
        typeOfService = buf.readByte();
        totalLength = buf.readShort();
        identification = buf.readShort();
        fragmentOffset = buf.readShort();
        timeToLive = buf.readByte();
        protocol = buf.readByte();
        checksum = buf.readShort();
        source = buf.readInt();
        destination = buf.readInt();
        int length = versionHeaderLength & 0x0F;
        options = length <= 5 ? new int[0] : new int[length - 5];
        for (int i = 0; i < options.length; i++)
            options[i] = buf.readInt();
        end = buf.getCursor();
    }

    public boolean is(Byte versionHeaderLength, Byte protocol, Integer source, Integer destination) {
        if (versionHeaderLength != null && this.versionHeaderLength != versionHeaderLength)
            return false;
        if (protocol != null && this.protocol != protocol)
            return false;
        if (source != null && this.source != source)
            return false;
        return destination == null || this.destination == destination;
    }

    @Override
    public int getExpectedHeaderSize() {
        return MIN_HEADER_SIZE; // + options
    }

    public byte getVersionHeaderLength() {
        return versionHeaderLength;
    }

    public byte getTypeOfService() {
        return typeOfService;
    }

    public short getTotalLength() {
        return totalLength;
    }

    public short getIdentification() {
        return identification;
    }

    public short getFragmentOffset() {
        return fragmentOffset;
    }

    public byte getTimeToLive() {
        return timeToLive;
    }

    public byte getProtocol() {
        return protocol;
    }

    public short getChecksum() {
        return checksum;
    }

    public int getSource() {
        return source;
    }

    public int getDestination() {
        return destination;
    }

    public static InternetProtocol forTCP(short id, int source, int destination) {
        return new InternetProtocol((byte) 0x45, (byte) 0, (short) 40, id, (short) 0x4000, (byte) 64, (byte) 0x06, (short) 0, source, destination, new int[0]);
    }
}
