package me.spencernold.jrs;

public class TCP {

    public static void sendTcpSyn(short sourcePort, int destinationIPv4, short destinationPort) {
        byte[] sourceMac = Mac.getSystemAddress();
        byte[] destinationMac = Mac.getRouterAddress();
        Ethernet eth = new Ethernet(sourceMac, destinationMac, (short) 0x0800);

        short id = (short) 69;
        int sourceIPv4 = IPv4.getSystemAddress();
        InternetProtocol ip = InternetProtocol.forTCP(id, sourceIPv4, destinationIPv4);

        TransmissionControlProtocol tcp = TransmissionControlProtocol.syn(sourcePort, destinationPort);

        NetworkStack stack = NetworkStack.tcp(eth, ip, tcp);
        NetworkByteBuf bufOut = stack.allocate();
        stack.write(bufOut);
        try (RawSocket socket = new RawSocket()) {
            socket.send(bufOut);
            socket.listen((bytes) -> {
                NetworkByteBuf bufIn = new NetworkByteBuf(bytes, true);
                stack.read(bufIn);
                if (!eth.is(destinationMac, sourceMac, (short) 0x0800))
                    return;
                if (!ip.is((byte) 0x45, (byte) 0x06, destinationIPv4, sourceIPv4))
                    return;
                if (!tcp.is(destinationPort, sourcePort))
                    return;
                System.out.println("Found packet!");
                socket.ignore();
            });
        } catch (Exception ignored) {
        }
    }
}
