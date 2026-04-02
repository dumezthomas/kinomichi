package be.technifutur.kinomichi.util;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;

public final class DateUtil {
    private static final DateTimeFormatter FORMATTER_DATE = DateTimeFormatter.ofPattern("dd-MM-yyyy");

    public static LocalDate parse(String input) throws DateTimeParseException {
        return LocalDate.parse(input, FORMATTER_DATE);
    }

    public static boolean isTodayOrFuture(LocalDate date) {
        return !date.isBefore(LocalDate.now());
    }

    public static boolean isTodayOrFuture(LocalDateTime date) {
        return !date.isBefore(LocalDateTime.now());
    }

    public static boolean isSaturday(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY;
    }

    public static boolean isSaturday(LocalDateTime date) {
        return date.getDayOfWeek() == DayOfWeek.SATURDAY;
    }

    public static boolean isSunday(LocalDate date) {
        return date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    public static boolean isSunday(LocalDateTime date) {
        return date.getDayOfWeek() == DayOfWeek.SUNDAY;
    }

    public static String format(LocalDate date) {
        return date.format(FORMATTER_DATE);
    }

    public static String formatWeekend(LocalDate date) {
        LocalDate start = date;
        LocalDate end = date.plusDays(1);

        String weekend = "Weekend du ";
        if (start.getYear() != end.getYear()) {
            weekend += start.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH));
        } else if (start.getMonth() != end.getMonth()) {
            weekend += start.format(DateTimeFormatter.ofPattern("d MMMM", Locale.FRENCH));
        } else {
            weekend += start.format(DateTimeFormatter.ofPattern("d", Locale.FRENCH));
        }

        return weekend + " - " + end.format(DateTimeFormatter.ofPattern("d MMMM yyyy", Locale.FRENCH));
    }
}