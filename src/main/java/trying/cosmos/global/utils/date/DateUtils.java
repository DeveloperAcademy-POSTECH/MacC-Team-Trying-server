package trying.cosmos.global.utils.date;

import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class DateUtils {

    public static String getFormattedDate(LocalDateTime dateTime) {
        return dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
    }

    public static String getFormattedDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    public static LocalDate stringToDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
        } catch (RuntimeException e) {
            throw new CustomException(ExceptionType.INVALID_INPUT);
        }
    }
}
