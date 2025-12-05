package me.spencernold.jrs;

public class Pointer<T> {

    private T value;

    public Pointer(T value) {
        this.value = value;
    }

    public Pointer() {
        this(null);
    }

    public void set(T value) {
        this.value = value;
    }

    public T get() {
        return value;
    }
}
