package me.spencernold.jrs;

import java.util.ArrayDeque;
import java.util.Deque;

public class NetworkStack {

    private final Deque<Protocol> stack;

    NetworkStack(Deque<Protocol> stack) {
        this.stack = stack;
    }

    private NetworkStack init() {
        for (Protocol protocol : stack)
            protocol.stack = this;
        return this;
    }

    public <T extends Protocol> T get(Class<T> clazz) {
        for (Protocol protocol : stack) {
            if (clazz.isAssignableFrom(protocol.getClass()))
                return clazz.cast(protocol);
        }
        return null;
    }

    public NetworkByteBuf allocate() {
        int size = 0;
        for (Protocol protocol : stack)
            size += protocol.getExpectedHeaderSize();
        return new NetworkByteBuf(size, false);
    }

    public void write(NetworkByteBuf buf) {
        for (Protocol protocol : stack)
            protocol.write(buf);
    }

    public void read(NetworkByteBuf buf) {
        for (Protocol protocol : stack)
            protocol.read(buf);
    }

    public static NetworkStack arp(Ethernet eth, AddressResolutionProtocol arp) {
        Deque<Protocol> stack = new ArrayDeque<>();
        stack.addLast(eth);
        stack.addLast(arp);
        return new NetworkStack(stack).init();
    }

    public static NetworkStack tcp(Ethernet eth, InternetProtocol ip, TransmissionControlProtocol tcp) {
        Deque<Protocol> stack = new ArrayDeque<>();
        stack.addLast(eth);
        stack.addLast(ip);
        stack.addLast(tcp);
        return new NetworkStack(stack).init();
    }
}
