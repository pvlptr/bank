package engineering.pvl.bank.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.time.Month;

import static org.junit.jupiter.api.Assertions.assertEquals;

class DateTimeUtilsTest {

    @AfterEach
    void tearDown() {
        DateTimeUtils.setCurrentSystem();
    }

    @Test
    void fromLocalDateTime() {
        LocalDateTime dateTime = LocalDateTime.of(2001, Month.MARCH, 1, 23, 39, 49);
        assertEquals("2001-03-01T23:39:49.000", DateTimeUtils.fromLocalDateTime(dateTime));
    }

    @Test
    void toLocalDateTime() {
        assertEquals(LocalDateTime.of(2001, Month.MARCH, 1, 23, 39, 49),
                DateTimeUtils.toLocalDateTime("2001-03-01T23:39:49.000"));
    }
}