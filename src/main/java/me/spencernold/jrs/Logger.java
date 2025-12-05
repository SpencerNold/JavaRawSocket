package me.spencernold.jrs;

public class Logger {

    // simple logger for now! Will make this more complex
    public static void info(String format, Object... args) {
        System.out.printf(format + "\n", args);
    }
}
