package me.spencernold.jrs;

public class Main {

    public static void main(String[] args) {
        try (RawSocket socket = new RawSocket()) {
            new Thread(() -> {
                socket.listen((s, data) -> {
                    System.out.println("Length: " + data.length);
                });
            }).start();
            while (true) {
                try {
                    Thread.sleep(1000L);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
