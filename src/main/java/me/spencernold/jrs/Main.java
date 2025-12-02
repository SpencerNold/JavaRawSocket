package me.spencernold.jrs;

public class Main {

    public static void main(String[] args) {
        int fd = Binding.tun_create();
        Binding.tun_bind(fd);
        Binding.tun_listen(fd);
        Binding.tun_free(fd);
    }
}
