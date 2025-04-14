package com.example.myapplication;

import com.example.myapplication.helper.DateHelper;
import com.example.myapplication.helper.DateHelper.DateFormats;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.Calendar;
import java.util.Locale;

import static org.junit.Assert.*;

public class ImprovedMutationTest {

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
    public void testGetDateOnlyStringAndLong() {
        long time = DateHelper.getDateOnly("01/01/2020");
        assertTrue(time > 0);
        String date = DateHelper.getDateOnly(time);
        assertEquals("01/01/2020", date);
    }

    @Test
    public void testGetTimeOnly() {
        long now = System.currentTimeMillis();
        String time = DateHelper.getTimeOnly(now);
        assertNotNull(time);
    }

    @Test
    public void testGetTodayAndTomorrow() {
        String today = DateHelper.getToday();
        String tomorrow = DateHelper.getTomorrow();
        assertNotNull(today);
        assertNotNull(tomorrow);
        assertNotEquals(today, tomorrow);
    }

    @Test
    public void testGetTodayWithTime() {
        String time = DateHelper.getTodayWithTime();
        assertNotNull(time);
    }


    @Test
    public void testParseDateAndAnyDate() {
        long parsed = DateHelper.parseDate("01-01-2020", DateFormats.D_DDMMYYYY);
        assertTrue(parsed > 0);
        long anyParsed = DateHelper.parseAnyDate("01/01/2020");
        assertTrue(anyParsed > 0);
    }

    @Test
    public void testGetDesiredFormat() {
        String now = DateHelper.getDesiredFormat(DateFormats.D_YYYYMMDD);
        assertNotNull(now);
        long timestamp = System.currentTimeMillis();
        String formatted = DateHelper.getDesiredFormat(DateFormats.D_YYYYMMDD, timestamp);
        assertNotNull(formatted);
    }

    @Test
    public void testGetDateFromDays() {
        String future = DateHelper.getDateFromDays(5);
        assertNotNull(future);
    }
}