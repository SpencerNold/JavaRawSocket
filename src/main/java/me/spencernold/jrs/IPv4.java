package me.spencernold.jrs;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class IPv4 {

    private static int ipv4Cache;
    private static boolean ipv4Stored;
    private static int defaultGatewayCache;
    private static boolean defaultGatewayStored;

    public static String decode(int ipv4) {
        return ((ipv4 >> 24) & 0xFF) + "." + ((ipv4 >> 16) & 0xFF) + "." + ((ipv4 >> 8) & 0xFF) + "." + (ipv4 & 0xFF);
    }

    public static int encode(String ipv4) {
        Pattern pattern = Pattern.compile("(?:[0-9]{1,3}\\.){3}[0-9]{1,3}");
        Matcher matcher = pattern.matcher(ipv4);
        if (!matcher.matches())
            throw new IllegalArgumentException(String.format("\"%s\" is not in the format of an ipv4 address", ipv4));
        String[] values = ipv4.split("\\.");
        int value = 0;
        for (int i = 0; i < 4; i++) {
            int b = Integer.parseInt(values[i]);
            if (b > 255)
                b = 255;
            if (b < 0)
                b = 0;
            value |= (b & 0xFF) << ((3 - i) * 8);
        }
        return value;
    }

    public static int getSystemAddress() {
        if (!ipv4Stored) {
            try {
                ipv4Cache = SystemBinding.getIPv4Address();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            ipv4Stored = true;
        }
        return ipv4Cache;
    }

    public static int getDefaultGateway() {
        if (!defaultGatewayStored) {
            try {
                defaultGatewayCache = SystemBinding.getDefaultGateway();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            defaultGatewayStored = true;
        }
        return defaultGatewayCache;
    }
}
