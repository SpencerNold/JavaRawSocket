package me.spencernold.jrs;

public class AddressResolutionProtocol extends Protocol {

    public static final int HEADER_SIZE = 28;

    private short hardwareType;
    private short protocolType;
    private byte hardwareLength;
    private byte protocolLength;
    private short operation;
    private byte[] senderHardwareAddress;
    private int senderProtocolAddress;
    private byte[] targetHardwareAddress;
    private int targetProtocolAddress;

    public AddressResolutionProtocol(short hardwareType, short protocolType, byte hardwareLength, byte protocolLength, short operation, byte[] senderHardwareAddress, int senderProtocolAddress, byte[] targetHardwareAddress, int targetProtocolAddress) {
        this.hardwareType = hardwareType;
        this.protocolType = protocolType;
        this.hardwareLength = hardwareLength;
        this.protocolLength = protocolLength;
        this.operation = operation;
        this.senderHardwareAddress = senderHardwareAddress;
        this.senderProtocolAddress = senderProtocolAddress;
        this.targetHardwareAddress = targetHardwareAddress;
        this.targetProtocolAddress = targetProtocolAddress;
    }

    @Override
    public void write(NetworkByteBuf buf) {
        buf.writeShort(hardwareType);
        buf.writeShort(protocolType);
        buf.writeByte(hardwareLength);
        buf.writeByte(protocolLength);
        buf.writeShort(operation);
        buf.write(senderHardwareAddress);
        buf.writeInt(senderProtocolAddress);
        buf.write(targetHardwareAddress);
        buf.writeInt(targetProtocolAddress);
    }

    @Override
    public void read(NetworkByteBuf buf) {
        hardwareType = buf.readShort();
        protocolType = buf.readShort();
        hardwareLength = buf.readByte();
        protocolLength = buf.readByte();
        operation = buf.readShort();
        senderHardwareAddress = buf.read(6);
        senderProtocolAddress = buf.readInt();
        targetHardwareAddress = buf.read(6);
        targetProtocolAddress = buf.readInt();
    }

    public boolean is(Short hardwareType, Short protocolType, Byte hardwareLength, Byte protocolLength, Short operation, byte[] senderHardwareAddress, Integer senderProtocolAddress, byte[] targetHardwareAddress, Integer targetProtocolAddress) {
        if (hardwareType != null && this.hardwareType != hardwareType)
            return false;
        if (protocolType != null && this.protocolType != protocolType)
            return false;
        if (hardwareLength != null && this.hardwareLength != hardwareLength)
            return false;
        if (protocolLength != null && this.protocolLength != protocolLength)
            return false;
        if (operation != null && this.operation != operation)
            return false;
        if (senderHardwareAddress != null && !NetworkByteBuf.compare(this.senderHardwareAddress, 0, senderHardwareAddress, 0, 6))
            return false;
        if (senderProtocolAddress != null && this.senderProtocolAddress != senderProtocolAddress)
            return false;
        if (targetHardwareAddress != null && !NetworkByteBuf.compare(this.targetHardwareAddress, 0, targetHardwareAddress, 0, 6))
            return false;
        return targetProtocolAddress == null || this.targetProtocolAddress == targetProtocolAddress;
    }

    public short getHardwareType() {
        return hardwareType;
    }

    public short getProtocolType() {
        return protocolType;
    }

    public byte getHardwareLength() {
        return hardwareLength;
    }

    public byte getProtocolLength() {
        return protocolLength;
    }

    public short getOperation() {
        return operation;
    }

    public byte[] getSenderHardwareAddress() {
        return senderHardwareAddress;
    }

    public int getSenderProtocolAddress() {
        return senderProtocolAddress;
    }

    public byte[] getTargetHardwareAddress() {
        return targetHardwareAddress;
    }

    public int getTargetProtocolAddress() {
        return targetProtocolAddress;
    }

    @Override
    public int getHeaderSize() {
        return HEADER_SIZE;
    }

    public static AddressResolutionProtocol ipv4(byte[] senderMacAddress, int senderIPv4Address, byte[] targetMacAddress, int targetIPv4Address) {
        return new AddressResolutionProtocol((short) 1, (short) 0x0800, (byte) 6, (byte) 4, (short) 1, senderMacAddress, senderIPv4Address, targetMacAddress, targetIPv4Address);
    }
}
