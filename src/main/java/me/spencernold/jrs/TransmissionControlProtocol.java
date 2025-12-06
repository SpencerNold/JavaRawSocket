package me.spencernold.jrs;

public class TransmissionControlProtocol extends Protocol {

    public static final int SYN = 0x02;

    public static final int MIN_HEADER_SIZE = 20;

    private short sourcePort;
    private short destinationPort;
    private int sequenceNumber;
    private int acknowledgeNumber;
    private byte offset;
    private byte flags;
    private short window;
    private short checksum;
    private short urgentPointer;
    private int[] options;
    private byte[] body;

    public TransmissionControlProtocol(short sourcePort, short destinationPort, int sequenceNumber, int acknowledgeNumber, byte offset, byte flags, short window, short checksum, short urgentPointer, int[] options, byte[] body) {
        this.sourcePort = sourcePort;
        this.destinationPort = destinationPort;
        this.sequenceNumber = sequenceNumber;
        this.acknowledgeNumber = acknowledgeNumber;
        this.offset = offset;
        this.flags = flags;
        this.window = window;
        this.checksum = checksum;
        this.urgentPointer = urgentPointer;
        this.options = options;
        this.body = body;
    }

    @Override
    public void write(NetworkByteBuf buf) {
        start = buf.getCursor();
        buf.writeShort(sourcePort);
        buf.writeShort(destinationPort);
        buf.writeInt(sequenceNumber);
        buf.writeInt(acknowledgeNumber);
        buf.writeByte(offset);
        buf.writeByte(flags);
        buf.writeShort(window);
        buf.writeShort(checksum);
        buf.writeShort(urgentPointer);
        for (int i : options)
            buf.writeInt(i);
        buf.write(body);
        end = buf.getCursor();

        InternetProtocol ip = stack.get(InternetProtocol.class);
        int pos = ip.getBufferStartPos() + 12;
        byte[] ipFrame = buf.internal(pos, pos + 8);

        int index = buf.getCursor();
        byte[] tcpFrame = buf.internal(start, end);

        NetworkByteBuf buf1 = new NetworkByteBuf(ipFrame.length + tcpFrame.length + 4, false);
        buf1.write(ipFrame);
        buf1.writeByte((byte) 0);
        buf1.writeByte((byte) 6);
        buf1.writeShort((short) 20);
        buf1.write(tcpFrame);

        byte[] internal = buf1.internal();
        short sum = ChecksumBinding.calculateIPv4Checksum(internal, 0, internal.length);

        buf.seek(start + 16);
        buf.writeShort(sum);
        buf.seek(index);
    }

    @Override
    public void read(NetworkByteBuf buf) {
        start = buf.getCursor();
        sourcePort = buf.readShort();
        destinationPort = buf.readShort();
        sequenceNumber = buf.readInt();
        acknowledgeNumber = buf.readInt();
        offset = buf.readByte();
        flags = buf.readByte();
        window = buf.readShort();
        checksum = buf.readShort();
        urgentPointer = buf.readShort();
        int length = (offset & 0xF0) >> 4;
        options = length <= 5 ? new int[0] : new int[length - 5];
        for (int i = 0; i < options.length; i++)
            options[i] = buf.readInt();
        body = buf.read(buf.remaining());
        end = buf.getCursor();
    }

    public boolean is(Short sourcePort, Short destinationPort) {
        if (sourcePort != null && this.sourcePort != sourcePort)
            return false;
        return destinationPort == null || this.destinationPort == destinationPort;
    }

    @Override
    public int getExpectedHeaderSize() {
        return MIN_HEADER_SIZE; // + payload
    }

    public short getSourcePort() {
        return sourcePort;
    }

    public short getDestinationPort() {
        return destinationPort;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public int getAcknowledgeNumber() {
        return acknowledgeNumber;
    }

    public byte getOffset() {
        return offset;
    }

    public byte getFlags() {
        return flags;
    }

    public short getWindow() {
        return window;
    }

    public short getChecksum() {
        return checksum;
    }

    public short getUrgentPointer() {
        return urgentPointer;
    }

    public static TransmissionControlProtocol syn(short sourcePort, short destinationPort) {
        return new TransmissionControlProtocol(sourcePort, destinationPort, 0, 0, (byte) 0x50, (byte) SYN, (short) 65535, (short) 0, (short) 0, new int[0], new byte[0]);
    }
}
