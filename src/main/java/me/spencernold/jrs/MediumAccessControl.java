package me.spencernold.jrs;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class MediumAccessControl {

    private static byte[] macCache = null;

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

    public static byte[] get() {
        if (macCache == null)
            macCache = SystemBinding.getMacAddress();
        return macCache;
    }
}
