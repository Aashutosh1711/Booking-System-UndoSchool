package com.bookingsystem.util;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility methods for timezone-aware display of session times.
 *
 * Rule: times are ALWAYS stored in the DB as UTC Instants.
 *       Conversion to a local timezone only happens when building
 *       response DTOs — never when persisting.
 */
public final class TimezoneUtil {

    /** ISO-8601 format including offset and zone name: "2025-06-07T18:00:00+05:30[Asia/Kolkata]" */
    private static final DateTimeFormatter FORMATTER =
            DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ssxxx'['VV']'");

    private TimezoneUtil() {}

    /**
     * Converts a UTC Instant to a formatted string in the given IANA timezone.
     *
     * @param utcInstant   the UTC timestamp from the database
     * @param ianaTimezone IANA zone ID, e.g. "Asia/Kolkata"
     * @return formatted string, e.g. "2025-06-07T18:00:00+05:30[Asia/Kolkata]"
     */
    public static String toLocalString(Instant utcInstant, String ianaTimezone) {
        ZoneId zoneId = ZoneId.of(ianaTimezone);
        ZonedDateTime local = utcInstant.atZone(zoneId);
        return local.format(FORMATTER);
    }

    /**
     * Validates that the given string is a recognized IANA timezone ID.
     *
     * @throws IllegalArgumentException if the timezone is unknown
     */
    public static void validateTimezone(String ianaTimezone) {
        try {
            ZoneId.of(ianaTimezone);
        } catch (Exception e) {
            throw new IllegalArgumentException(
                    "Unknown timezone: '" + ianaTimezone + "'. " +
                    "Use an IANA timezone ID such as 'Asia/Kolkata' or 'America/New_York'.");
        }
    }
}
