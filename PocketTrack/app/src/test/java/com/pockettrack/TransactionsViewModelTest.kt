package com.pockettrack

import android.app.Application
import androidx.test.core.app.ApplicationProvider
import com.pockettrack.ui.transactions.TransactionsViewModel
import org.junit.Assert.assertTrue
import org.junit.Test

class TransactionsViewModelTest {
    @Test
    fun monthBounds_areOrdered() {
        val app = ApplicationProvider.getApplicationContext<Application>()
        val vm = TransactionsViewModel(app)
        val (s, e) = vm.monthBounds()
        assertTrue(s &lt; e)
    }
}