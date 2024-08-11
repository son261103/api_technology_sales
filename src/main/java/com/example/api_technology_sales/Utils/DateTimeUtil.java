package com.example.api_technology_sales.Utils;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;

public class DateTimeUtil {

    private static final String DEFAULT_DATE_FORMAT = "dd/MM/yyyy HH:mm:ss";
    private static final ZoneId DEFAULT_ZONE_ID = ZoneId.systemDefault();

    public static LocalDateTime getCurrentDateTime() {
        return LocalDateTime.now(DEFAULT_ZONE_ID);
    }

    public static String formatDateTime(LocalDateTime dateTime) {
        return formatDateTime(dateTime, DEFAULT_DATE_FORMAT);
    }

    public static String formatDateTime(LocalDateTime dateTime, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return dateTime.format(formatter);
    }

    public static LocalDateTime parseDateTime(String dateTimeString) {
        return parseDateTime(dateTimeString, DEFAULT_DATE_FORMAT);
    }

    public static LocalDateTime parseDateTime(String dateTimeString, String format) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern(format);
        return LocalDateTime.parse(dateTimeString, formatter);
    }

    public static boolean isDateTimeAfter(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.isAfter(dateTime2);
    }

    public static boolean isDateTimeBefore(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return dateTime1.isBefore(dateTime2);
    }

    public static long getDaysBetween(LocalDateTime dateTime1, LocalDateTime dateTime2) {
        return java.time.Duration.between(dateTime1, dateTime2).toDays();
    }
}