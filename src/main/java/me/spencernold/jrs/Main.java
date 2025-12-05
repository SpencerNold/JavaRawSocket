package me.spencernold.jrs;

public class Main {

    public static void main(String[] args) {
        int tpa = InternetProtocol4.encode("10.0.0.1");
        byte[] mac = Common.sendMacRequest(tpa);
        System.out.println(mac == null ? "null" : MediumAccessControl.decode(mac));
    }
}
