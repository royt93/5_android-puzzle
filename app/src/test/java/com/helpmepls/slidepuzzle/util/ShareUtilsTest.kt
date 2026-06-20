package com.helpmepls.slidepuzzle.util

import org.junit.Assert.assertEquals
import org.junit.Test

class ShareUtilsTest {

    @Test
    fun formatTimeZeroSeconds() {
        assertEquals("00:00", ShareUtils.formatTime(0))
    }

    @Test
    fun formatTimeUnderOneMinute() {
        assertEquals("00:45", ShareUtils.formatTime(45))
    }

    @Test
    fun formatTimeExactlyOneMinute() {
        assertEquals("01:00", ShareUtils.formatTime(60))
    }

    @Test
    fun formatTimeWithMinutesAndSeconds() {
        assertEquals("01:05", ShareUtils.formatTime(65))
    }

    @Test
    fun formatTimeLargeValue() {
        assertEquals("10:00", ShareUtils.formatTime(600))
    }

    @Test
    fun formatTimeSecondsAlwaysTwoDigits() {
        assertEquals("02:03", ShareUtils.formatTime(123))
    }
}
