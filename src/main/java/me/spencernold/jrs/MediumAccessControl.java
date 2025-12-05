package me.spencernold.jrs;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediumAccessControl {

    private static byte[] systemMacCache = null;
    private static byte[] routerMacCache = null;
    private static int currentRouterIPv4 = 0;

    public static byte[] encode(String mac) {
        Pattern pattern = Pattern.compile("(?:[A-Fa-f0-9]{2}:){5}[A-Fa-f0-9]{2}");
        Matcher matcher = pattern.matcher(mac);
        if (!matcher.matches())
            throw new IllegalArgumentException(String.format("\"%s\" is not in the format of a mac address", mac));
        byte[] bytes = new byte[6];
        String[] values = mac.split(":");
        for (int i = 0; i < 6; i++)
            bytes[i] = (byte) Integer.parseInt(values[i], 16);
        return bytes;
    }

    public static String decode(byte[] bytes) {
        if (bytes.length != 6)
            throw new IllegalArgumentException(String.format("input %d bytes, mac addresses must be 6", bytes.length));
        String[] values = new String[6];
        for (int i = 0; i < 6; i++)
            values[i] = String.format("%02X", bytes[i] & 0xFF);
        return String.join(":", values);
    }

    public static byte[] getSystemMac() {
        if (systemMacCache == null) {
            try {
                systemMacCache = SystemBinding.getMacAddress();
            } catch (IOException e) {
                throw new RuntimeException(e); // Shouldn't* happen, why wouldn't the computer know it's own mac?
            }
        }
        return systemMacCache;
    }

    public static byte[] getRouterMac() {
        int gateway = InternetProtocol4.getDefaultGateway();
        if (routerMacCache == null || gateway != currentRouterIPv4) {
            currentRouterIPv4 = gateway;
            routerMacCache = getStoredMac(gateway);
        }
        return routerMacCache;
    }

    public static byte[] getStoredMac(int ipv4) {
        try {
            return SystemBinding.getDeviceMacAddress(ipv4);
        } catch (IOException ignored) {
            Logger.info("Sending ARP request to %s...", InternetProtocol4.decode(ipv4));
            return ARP.sendMacRequest(ipv4);
        }
    }
}
