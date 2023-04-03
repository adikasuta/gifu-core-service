package com.gifu.coreservice.utils;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;

public class DateUtils {

    private static final ZoneId zoneId = ZoneId.of("Asia/Jakarta");

    public static ZonedDateTime toZoneDateTime(LocalDate localDate, boolean startDay) {
        LocalTime localTime = LocalTime.of(0, 0, 0);
        if (!startDay) {
            localTime = LocalTime.of(23, 59, 59);
        }
        return ZonedDateTime.of(localDate, localTime, zoneId);
    }

}
