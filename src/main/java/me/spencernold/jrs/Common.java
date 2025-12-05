package me.spencernold.jrs;

public class Common {

    public static byte[] sendMacRequest(int ipv4) {
        byte[] src = MediumAccessControl.getSystemMac();
        byte[] dst = new byte[]{(byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF, (byte) 0xFF};
        Ethernet eth = new Ethernet(src, dst, (short) 0x0806);
        int spa = InternetProtocol4.getSystemIPv4();
        AddressResolutionProtocol arp = AddressResolutionProtocol.ipv4(src, spa, dst, ipv4);
        NetworkStack stack = NetworkStack.arp(eth, arp);
        NetworkByteBuf bufOut = stack.allocate();
        stack.write(bufOut);
        Pointer<byte[]> pointer = new Pointer<>();
        try (RawSocket socket = new RawSocket()) {
            socket.send(bufOut);
            socket.listen((bytes) -> {
                NetworkByteBuf bufIn = new NetworkByteBuf(bytes, true);
                stack.read(bufIn);
                if (!eth.is(null, src, (short) 0x0806))
                    return;
                if (!arp.is((short) 1, (short) 0x0800, (byte) 6, (byte) 4, (short) 2, null, ipv4, src, spa))
                    return;
                pointer.set(arp.getSenderHardwareAddress());
                socket.ignore();
            });
        } catch (Exception e) {
            return null;
        }
        return pointer.get();
    }
}
