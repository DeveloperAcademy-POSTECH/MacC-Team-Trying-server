package trying.cosmos.global.utils;

import trying.cosmos.global.exception.CustomException;
import trying.cosmos.global.exception.ExceptionType;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public interface DateUtils {

    static String getFormattedDate(LocalDate date) {
        return date.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }

    static LocalDate stringToDate(String dateString) {
        try {
            return LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE);
        } catch (RuntimeException e) {
            throw new CustomException(ExceptionType.INVALID_INPUT);
        }
    }
}
