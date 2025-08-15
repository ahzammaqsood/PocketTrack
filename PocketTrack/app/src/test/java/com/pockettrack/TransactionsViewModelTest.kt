package com.pockettrack

import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class TransactionsViewModelTest {
    @Test
    fun monthBounds_areOrdered() {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        cal.add(Calendar.MILLISECOND, -1)
        val end = cal.timeInMillis
        assertTrue(start < end)
    }
}