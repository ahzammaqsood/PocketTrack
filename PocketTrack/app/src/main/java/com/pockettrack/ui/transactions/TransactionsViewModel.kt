package com.pockettrack.ui.transactions

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import com.pockettrack.data.Repository
import com.pockettrack.data.entity.TransactionEntity
import java.util.Calendar

class TransactionsViewModel(app: Application) : AndroidViewModel(app) {
    private val repo = Repository(app)

    private val filterCategory = MutableLiveData<String?>(null)
    private val filterStart = MutableLiveData<Long>(monthBounds().first)
    private val filterEnd = MutableLiveData<Long>(monthBounds().second)

    val transactions: LiveData<List<TransactionEntity>> = Transformations.switchMap(filterCategory) { cat ->
        Transformations.switchMap(filterStart) { s ->
            Transformations.switchMap(filterEnd) { e ->
                repo.observeFiltered(cat, s, e)
            }
        }
    }

    fun setCategory(cat: String?) { filterCategory.value = cat }
    fun setDateRange(start: Long, end: Long) { filterStart.value = start; filterEnd.value = end }

    fun monthBounds(cal: Calendar = Calendar.getInstance()): Pair<Long, Long> {
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.add(Calendar.MONTH, 1)
        cal.add(Calendar.MILLISECOND, -1)
        val end = cal.timeInMillis
        return start to end
    }
}