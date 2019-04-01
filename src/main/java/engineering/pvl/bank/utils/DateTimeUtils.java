package engineering.pvl.bank.utils;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.time.temporal.TemporalAccessor;

public class DateTimeUtils {

    private static Clock currentClock = Clock.systemDefaultZone();

    private DateTimeUtils() {
    }


    public static LocalDateTime nowUTC() {
        return now().atZone(ZoneId.systemDefault()).withZoneSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    private static LocalDateTime now() {
        return LocalDateTime.now(currentClock);
    }

    private static final ThreadLocal<DateTimeFormatter> threadLocalDateTimeFormatter =
            ThreadLocal.withInitial(() -> new DateTimeFormatterBuilder().appendPattern(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS").toFormatter());

    public static String fromLocalDateTime(LocalDateTime localDateTime) {
        return localDateTime.format(threadLocalDateTimeFormatter.get());
    }

    public static LocalDateTime toLocalDateTime(String iso8601string) {
        TemporalAccessor temporalAccessor = threadLocalDateTimeFormatter.get().parse(iso8601string);

        if (temporalAccessor.isSupported(ChronoField.OFFSET_SECONDS)) {
            return ZonedDateTime.parse(iso8601string, threadLocalDateTimeFormatter.get())
                    .withFixedOffsetZone().toLocalDateTime();
        } else {
            return LocalDateTime.parse(iso8601string, threadLocalDateTimeFormatter.get());
        }
    }

    public static void setCurrentFixed(int year, Month month, int day,
                                       int hour, int minute, int second) {
        LocalDateTime localDateTime = LocalDateTime.of(year, month, day, hour,
                minute, second);
        currentClock = Clock.fixed(localDateTime.toInstant(ZoneOffset.UTC),
                ZoneId.ofOffset("UTC", ZoneOffset.UTC));
    }

    public static void setCurrentSystem() {
        currentClock = Clock.systemDefaultZone();
    }


}
