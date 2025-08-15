package com.pockettrack.ui.transactions

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import com.pockettrack.R
import com.pockettrack.data.entity.TransactionEntity
import com.pockettrack.databinding.DialogAddEditTransactionBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class AddEditTransactionDialogFragment : DialogFragment() {
    private var _binding: DialogAddEditTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionsViewModel by viewModels({ requireParentFragment() })

    private var editItem: TransactionEntity? = null
    private var selectedDateMillis: Long = System.currentTimeMillis()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        editItem = arguments?.getParcelable("item") // not used for now; simple add
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        _binding = DialogAddEditTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        val categories = resources.getStringArray(R.array.default_categories).toList()
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, categories)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spCategory.adapter = adapter

        binding.rbExpense.isChecked = true
        updateDateButton()

        binding.btnDate.setOnClickListener { pickDate() }
        binding.btnCancel.setOnClickListener { dismiss() }
        binding.btnSave.setOnClickListener { save() }
    }

    private fun pickDate() {
        val cal = Calendar.getInstance().apply { timeInMillis = selectedDateMillis }
        DatePickerDialog(requireContext(), { _, y, m, d ->
            cal.set(y, m, d, 12, 0, 0)
            cal.set(Calendar.MILLISECOND, 0)
            selectedDateMillis = cal.timeInMillis
            updateDateButton()
        }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH)).show()
    }

    private fun updateDateButton() {
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        binding.btnDate.text = df.format(Date(selectedDateMillis))
    }

    private fun save() {
        val amount = binding.etAmount.text.toString().toDoubleOrNull()
        val category = binding.spCategory.selectedItem?.toString() ?: "Other"
        val note = binding.etNote.text?.toString()
        val type = if (binding.rbIncome.isChecked) "income" else "expense"

        if (amount == null || amount <= 0.0) {
            Toast.makeText(requireContext(), "Enter a valid amount", Toast.LENGTH_SHORT).show()
            return
        }

        val tx = editItem?.copy(
            type = type,
            amount = amount,
            category = category,
            note = note,
            date = selectedDateMillis
        ) ?: TransactionEntity(type = type, amount = amount, category = category, note = note, date = selectedDateMillis)

        viewLifecycleOwner.lifecycleScope.launch {
            // Use repository through ViewModel
            com.pockettrack.data.Repository(requireContext()).upsert(tx)
            dismiss()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}