package me.spencernold.jrs;

public class Main {

    public static void main(String[] args) {
        int fd = Binding.tun_create();
        System.out.println("Desc: " + fd);
        Binding.tun_free(fd);
    }
}
