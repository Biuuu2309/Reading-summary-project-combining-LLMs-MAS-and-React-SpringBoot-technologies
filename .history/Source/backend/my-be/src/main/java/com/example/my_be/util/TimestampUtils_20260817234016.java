package com.example.my_be.util;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class TimestampUtils {

    private static final ZoneId ZONE = ZoneId.of("Asia/Ho_Chi_Minh");
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.ENGLISH);

    private TimestampUtils() {
    }

    public static String now() {
        return ZonedDateTime.now(ZONE).format(FORMATTER);
    }
}
