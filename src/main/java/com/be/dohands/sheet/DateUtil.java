package com.be.dohands.sheet;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class DateUtil {

    private DateUtil() {
    }

    public static LocalDate toLocalDate(Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof LocalDate) {
            return (LocalDate) object; // Already LocalDate
        }

        if (object instanceof String stringDate) {
            try {
                List<DateTimeFormatter> formatters = List.of(
                    DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX"),
                    DateTimeFormatter.ofPattern("yyyy-MM-dd")
                );

                for (DateTimeFormatter formatter : formatters) {
                    try {
                        return LocalDate.parse(stringDate, formatter);
                    } catch (DateTimeParseException e) {
                        // 다음 포매팅 시도
                    }
                }

            } catch (DateTimeParseException e) {
                log.info("LocalDate로 변환 실패 : " + stringDate);
            }
        }

        throw new IllegalArgumentException("잘못된 인스턴스 타입입니다 : " + object.getClass().getName());
    }

    public static LocalDateTime toLocalDateTime(Object object) {
        if (object == null) {
            return null;
        }

        if (object instanceof LocalDateTime) {
            return (LocalDateTime) object; // Already LocalDate
        }

        if (object instanceof String stringDate) {
            try {
                DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSSX");
                return LocalDateTime.parse(stringDate, formatter);
            } catch (DateTimeParseException e) {
                log.info("LocalDateTime으로 변환 실패 : " + stringDate);
            }
        }

        throw new IllegalArgumentException("잘못된 인스턴스 타입입니다");
    }

    public static String localDateToString(LocalDate localDate) {
        return localDate.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
    }
}
