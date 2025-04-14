package com.example.myapplication;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import com.example.myapplication.helper.DateHelper;
import com.example.myapplication.helper.DateHelper.DateFormats;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Locale;

public class ImprovedCoverageTest {

    private Locale defaultLocale;

    @Before
    public void setUp() {
        defaultLocale = Locale.getDefault();
        Locale.setDefault(Locale.US);
    }

    @After
    public void tearDown() {
        Locale.setDefault(defaultLocale);
    }

    @Test
    public void testGetDateOnly_fromLong() {
        long currentTime = System.currentTimeMillis();
        String result = DateHelper.getDateOnly(currentTime);
        assertNotNull(result);
        assertTrue(result.matches("\\d{2}/\\d{2}/\\d{4}"));
    }

    @Test
    public void testGetDateOnly_fromString() {
        long millis = DateHelper.getDateOnly("01/01/2020");
        assertTrue(millis > 0);
    }

    @Test
    public void testPrettifyDate_longAndString() {
        long now = System.currentTimeMillis();
        assertNotNull(DateHelper.prettifyDate(now));
        assertNotNull(DateHelper.prettifyDate(Long.toString(now)));
    }

    @Test
    public void testGetDateAndTime_longAndString() {
        long now = System.currentTimeMillis();
        assertNotNull(DateHelper.getDateAndTime(now));
        assertNotNull(DateHelper.getDateAndTime(Long.toString(now)));
    }

    @Test
    public void testGetTimeOnly() {
        assertNotNull(DateHelper.getTimeOnly(System.currentTimeMillis()));
    }

    @Test
    public void testGetTodayAndTomorrow() {
        assertNotNull(DateHelper.getToday());
        assertNotNull(DateHelper.getTodayWithTime());
        assertNotNull(DateHelper.getTomorrow());
    }

    @Test
    public void testGetDaysBetweenTwoDates() {
        Long days = DateHelper.getDaysBetweenTwoDate("01/01/2020, 10:00 AM", "02/01/2020, 10:00 AM", DateFormats.D_DDMMYYYYHHMMA);
        assertEquals(Long.valueOf(-1), days);
    }

    @Test
    public void testGetHoursBetweenTwoDates() {
        Long hours = DateHelper.getHoursBetweenTwoDate("01/01/2020, 10:00 AM", "01/01/2020, 12:00 PM", DateFormats.D_DDMMYYYYHHMMA);
        assertEquals(Long.valueOf(-2), hours);
    }

    @Test
    public void testGetMinutesBetweenTwoDates() {
        Long minutes = DateHelper.getMinutesBetweenTwoDates("01/01/2020, 10:00 AM", "01/01/2020, 10:30 AM", DateFormats.D_DDMMYYYYHHMMA);
        assertEquals(Long.valueOf(-30), minutes);
    }

    @Test
    public void testParseAnyDate() {
        long result = DateHelper.parseAnyDate("2020-01-01");
        assertTrue(result > 0);
    }

    @Test
    public void testParseDate() {
        long result = DateHelper.parseDate("01-01-2020", DateFormats.D_DDMMYYYY);
        assertTrue(result > 0);
    }

    @Test
    public void testGetDesiredFormat() {
        assertNotNull(DateHelper.getDesiredFormat(DateFormats.D_DDMMYYYY));
        assertNotNull(DateHelper.getDesiredFormat(DateFormats.D_DDMMYYYY, System.currentTimeMillis()));
    }

    @Test
    public void testGetDateFromDays() {
        String result = DateHelper.getDateFromDays(5);
        assertNotNull(result);
    }
}
