package ru.practicum.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.time.format.DateTimeFormatter;

public class Constant {
    public static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static final String DATE_FORMAT = "yyyy-MM-dd HH:mm:ss";
    public static final String APP_NAME = "ewm-main-service";
    private static InetAddress ip;
    public static final String IP;

    static {
        try {
            ip = InetAddress.getLocalHost();
            IP = ip.getHostAddress();
        } catch (UnknownHostException e) {
            throw new RuntimeException(e);
        }
    }
}
