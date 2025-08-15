package com.pockettrack.ui.transactions

import android.app.DatePickerDialog
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SearchView
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.pockettrack.R
import com.pockettrack.data.Repository
import com.pockettrack.data.entity.TransactionEntity
import com.pockettrack.databinding.FragmentTransactionListBinding
import com.pockettrack.util.ExportUtils
import com.pockettrack.util.ThemeManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class TransactionListFragment : Fragment() {

    private var _binding: FragmentTransactionListBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionsViewModel by viewModels()
    private val adapter = TransactionsAdapter(
        onClick = { item -> showAddEdit(item) },
        onDelete = { item -> deleteItem(item) }
    )

    private var lastList: List<TransactionEntity> = emptyList()
    private var currentQuery: String = ""

    private val repo by lazy { Repository(requireContext()) }

    private val createCsv = registerForActivityResult(ActivityResultContracts.CreateDocument("text/csv")) { uri: Uri? ->
        uri?.let { ExportUtils.writeCsv(requireContext(), it, filterForExport()) }
        (activity as? com.pockettrack.ui.MainActivity)?.maybeShowInterstitial()
    }
    private val createPdf = registerForActivityResult(ActivityResultContracts.CreateDocument("application/pdf")) { uri: Uri? ->
        uri?.let { ExportUtils.writePdf(requireContext(), it, filterForExport()) }
        (activity as? com.pockettrack.ui.MainActivity)?.maybeShowInterstitial()
    }

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
            lastList = list
            applySearchAndRender()
            binding.empty.isVisible = list.isEmpty()
        })

        binding.btnFilterDate.setOnClickListener { pickDateRange() }
        binding.btnFilterCategory.setOnClickListener { showCategoryMenu() }

        setupBarChart()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_main, menu)
        val searchItem = menu.findItem(R.id.action_search)
        val searchView = searchItem.actionView as SearchView
        searchView.queryHint = getString(R.string.search)
        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                currentQuery = query ?: ""
                applySearchAndRender()
                return true
            }
            override fun onQueryTextChange(newText: String?): Boolean {
                currentQuery = newText ?: ""
                applySearchAndRender()
                return true
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_add -> { showAddEdit(null); true }
            R.id.action_export_csv -> { doExportCsv(); true }
            R.id.action_export_pdf -> { doExportPdf(); true }
            R.id.action_theme -> { toggleTheme(); true }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showAddEdit(item: TransactionEntity?) {
        val dlg = AddEditTransactionDialogFragment()
        if (item != null) {
            val b = Bundle()
            b.putParcelable("item", item)
            dlg.arguments = b
        }
        dlg.show(childFragmentManager, "add_edit")
    }

    private fun deleteItem(item: TransactionEntity) {
        AlertDialog.Builder(requireContext())
            .setMessage("Delete this transaction?")
            .setPositiveButton(R.string.delete) { _, _ ->
                viewLifecycleOwner.lifecycleScope.launch { repo.delete(item) }
            }
            .setNegativeButton(R.string.cancel, null)
            .show()
    }

    private fun doExportCsv() {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
        createCsv.launch("pockettrack_${sdf.format(Date())}.csv")
    }

    private fun doExportPdf() {
        val sdf = SimpleDateFormat("yyyyMMdd_HHmm", Locale.getDefault())
        createPdf.launch("pockettrack_${sdf.format(Date())}.pdf")
    }

    private fun toggleTheme() {
        viewLifecycleOwner.lifecycleScope.launch {
            val dark = ThemeManager.isDarkFlow(requireContext()).first()
            ThemeManager.setDark(requireContext(), !dark)
        }
    }

    private fun applySearchAndRender() {
        val filtered = if (currentQuery.isBlank()) lastList else lastList.filter {
            it.category.contains(currentQuery, true) || (it.note?.contains(currentQuery, true) ?: false)
        }
        adapter.submitList(filtered)
        renderSummary(filtered)
    }

    private fun setupBarChart() {
        binding.barChart.axisRight.isEnabled = false
        binding.barChart.description.isEnabled = false
        binding.barChart.xAxis.position = XAxis.XAxisPosition.BOTTOM
        binding.barChart.xAxis.granularity = 1f
    }

    private fun renderSummary(list: List<TransactionEntity>) {
        val income = list.filter { it.type == "income" }.sumOf { it.amount }
        val expense = list.filter { it.type == "expense" }.sumOf { it.amount }
        val fmt = NumberFormat.getCurrencyInstance()
        binding.txtIncome.text = "Income: ${fmt.format(income)}"
        binding.txtExpense.text = "Expense: ${fmt.format(expense)}"

        // Pie Chart by category (expenses only)
        val byCat = list.filter { it.type == "expense" }
            .groupBy { it.category }
            .mapValues { it.value.sumOf { t -> t.amount } }
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
        binding.pieChart.data = PieData(dataSet)
        binding.pieChart.description.isEnabled = false
        binding.pieChart.invalidate()

        // Bar Chart: total per day (expenses only)
        val cal = Calendar.getInstance()
        val map = sortedMapOf<Int, Double>()
        list.filter { it.type == "expense" }.forEach { t ->
            cal.timeInMillis = t.date
            val day = cal.get(Calendar.DAY_OF_MONTH)
            map[day] = (map[day] ?: 0.0) + t.amount
        }
        val barEntries = map.entries.map { BarEntry(it.key.toFloat(), it.value.toFloat()) }
        val barDataSet = BarDataSet(barEntries, "Daily Expense")
        binding.barChart.data = BarData(barDataSet)
        binding.barChart.invalidate()
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
        val cats = resources.getStringArray(R.array.default_categories)
        val items = arrayOf("All") + cats
        AlertDialog.Builder(requireContext())
            .setTitle(R.string.filter)
            .setItems(items) { _, which ->
                viewModel.setCategory(if (which == 0) null else items[which])
            }
            .show()
    }

    private fun filterForExport(): List<TransactionEntity> = adapter.currentList
}