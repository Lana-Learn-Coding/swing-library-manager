package io.lana.library.utils;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;

public class DateFormatUtils {
    public static final DateTimeFormatter COMMON_DATE_FORMAT = DateTimeFormatter.ofPattern("yyy-MM-dd[ HH:mm:ss]");

    private static final DateTimeFormatter COMMON_DATE_FORMAT_WITHOUT_TIME = DateTimeFormatter.ofPattern("yyy-MM-dd");

    private DateFormatUtils() {
    }

    public static String toDateString(LocalDate localDate) {
        return localDate.format(COMMON_DATE_FORMAT_WITHOUT_TIME);
    }

    public static String toDateString(LocalDateTime localDateTime) {
        return localDateTime.format(COMMON_DATE_FORMAT);
    }

    public static String toDateString(Date date) {
        if (date instanceof java.sql.Date) {
            LocalDate localDate = new java.sql.Date(date.getTime()).toLocalDate();
            return toDateString(localDate);
        }
        LocalDateTime localDateTime = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        return toDateString(localDateTime);
    }

    public static String toDateString(Object date) {
        if (date == null) {
            return null;
        }

        if (date instanceof LocalDate) {
            return toDateString((LocalDate) date);
        } else if (date instanceof LocalDateTime) {
            return toDateString((LocalDateTime) date);
        } else if (date instanceof Date) {
            return toDateString((Date) date);
        }

        throw new RuntimeException("Not supported date type: " + date.getClass());
    }

    public static String toDateStringWithDefaultUnknown(Object date) {
        String parsed = toDateString(date);
        return parsed == null ? "Unknown" : parsed;
    }
}
