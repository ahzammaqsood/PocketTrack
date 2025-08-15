package com.pockettrack.ui.transactions

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.pockettrack.R
import com.pockettrack.data.entity.TransactionEntity
import com.pockettrack.databinding.FragmentTransactionListBinding
import java.text.NumberFormat
import java.util.Calendar

class TransactionListFragment : Fragment() {

    private var _binding: FragmentTransactionListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionsViewModel by viewModels()
    private val adapter = TransactionsAdapter(
        onClick = { /* open edit */ },
        onDelete = { /* show confirm then delete */ }
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = FragmentTransactionListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.recycler.layoutManager = LinearLayoutManager(requireContext())
        binding.recycler.adapter = adapter

        viewModel.transactions.observe(viewLifecycleOwner, Observer { list ->
            adapter.submitList(list)
            renderSummary(list)
            binding.empty.isVisible = list.isEmpty()
        })

        binding.btnFilterDate.setOnClickListener { pickDateRange() }
        binding.btnFilterCategory.setOnClickListener { showCategoryMenu() }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> { /* open add */ true }
            R.id.action_export_csv -> { /* export csv */ true }
            R.id.action_export_pdf -> { /* export pdf */ true }
            R.id.action_theme -> { /* toggle theme */ true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun renderSummary(list: List<TransactionEntity>) {
        val income = list.filter { it.type == "income" }.sumOf { it.amount }
        val expense = list.filter { it.type == "expense" }.sumOf { it.amount }
        val fmt = NumberFormat.getCurrencyInstance()
        binding.txtIncome.text = "Income: ${fmt.format(income)}"
        binding.txtExpense.text = "Expense: ${fmt.format(expense)}"

        // Pie Chart by category (expenses only)
        val byCat = list.filter { it.type == "expense" }.groupBy { it.category }.mapValues { it.value.sumOf { t -> t.amount } }
        val entries = byCat.entries.map { PieEntry(it.value.toFloat(), it.key) }
        val dataSet = PieDataSet(entries, "Expenses by Category")
        dataSet.setColors(intArrayOf(
            android.graphics.Color.parseColor("#F44336"),
            android.graphics.Color.parseColor("#2196F3"),
            android.graphics.Color.parseColor("#4CAF50"),
            android.graphics.Color.parseColor("#FF9800"),
            android.graphics.Color.parseColor("#9C27B0"),
            android.graphics.Color.parseColor("#00BCD4")
        ), 255)
        val data = PieData(dataSet)
        binding.pieChart.data = data
        binding.pieChart.description.isEnabled = false
        binding.pieChart.invalidate()
    }

    private fun pickDateRange() {
        val cal = Calendar.getInstance()
        DatePickerDialog(requireContext(), { _, y, m, d ->
            val startCal = Calendar.getInstance().apply { set(y, m, d, 0, 0, 0); set(Calendar.MILLISECOND, 0) }
            val endCal = Calendar.getInstance().apply { set(y, m, d, 23, 59, 59); set(Calendar.MILLISECOND, 999) }
            viewModel.setDateRange(startCal.timeInMillis, endCal.timeInMillis)
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun showCategoryMenu() {
        // For brevity, just cycle between null and "Food"
        val current = (0..1).random()
        viewModel.setCategory(if (current == 0) null else "Food")
    }
}