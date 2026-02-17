package com.coanalysis.server.infrastructure.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.util.Date;

/**
 * 시간대 변환 유틸리티 클래스
 * - 저장: UTC (ZoneOffset.UTC)
 * - 조회: KST (Asia/Seoul, UTC+9)
 */
public final class TimeZoneUtil {

    public static final ZoneId UTC = ZoneId.of("UTC");
    public static final ZoneId KST = ZoneId.of("Asia/Seoul");

    private TimeZoneUtil() {
    }

    /**
     * Unix timestamp (초 단위)를 UTC LocalDateTime으로 변환
     */
    public static LocalDateTime fromEpochSecondToUtc(long epochSecond) {
        return LocalDateTime.ofInstant(Instant.ofEpochSecond(epochSecond), UTC);
    }

    /**
     * Unix timestamp (밀리초 단위)를 UTC LocalDateTime으로 변환
     */
    public static LocalDateTime fromEpochMilliToUtc(long epochMilli) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(epochMilli), UTC);
    }

    /**
     * Date 객체를 UTC LocalDateTime으로 변환
     */
    public static LocalDateTime fromDateToUtc(Date date) {
        if (date == null) {
            return null;
        }
        return LocalDateTime.ofInstant(date.toInstant(), UTC);
    }

    /**
     * 현재 시간을 UTC LocalDateTime으로 반환
     */
    public static LocalDateTime nowUtc() {
        return LocalDateTime.now(UTC);
    }

    /**
     * UTC LocalDateTime을 KST LocalDateTime으로 변환 (조회용)
     */
    public static LocalDateTime toKst(LocalDateTime utcTime) {
        if (utcTime == null) {
            return null;
        }
        ZonedDateTime utcZoned = utcTime.atZone(UTC);
        return utcZoned.withZoneSameInstant(KST).toLocalDateTime();
    }

    /**
     * KST LocalDateTime을 UTC LocalDateTime으로 변환 (저장용)
     */
    public static LocalDateTime toUtc(LocalDateTime kstTime) {
        if (kstTime == null) {
            return null;
        }
        ZonedDateTime kstZoned = kstTime.atZone(KST);
        return kstZoned.withZoneSameInstant(UTC).toLocalDateTime();
    }
}
