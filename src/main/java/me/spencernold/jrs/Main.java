package me.spencernold.jrs;

public class Main {

    public static void main(String[] args) {
        int destIPv4 = IPv4.encode("198.35.26.98");
        TCP.sendTcpSyn((short) 25565, destIPv4, (short) 443);
    }
}
