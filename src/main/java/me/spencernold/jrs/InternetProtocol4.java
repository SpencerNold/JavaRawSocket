package me.spencernold.jrs;

public class InternetProtocol4 {

    public static String decodeAddress(int ipv4) {
        return  ((ipv4 >> 24) & 0xFF) + "." + ((ipv4 >> 16) & 0xFF) + "." + ((ipv4 >> 8)  & 0xFF) + "." + (ipv4 & 0xFF);
    }
}
