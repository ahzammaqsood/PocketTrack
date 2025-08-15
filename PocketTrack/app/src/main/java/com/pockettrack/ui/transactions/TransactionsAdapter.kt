package com.pockettrack.ui.transactions

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.pockettrack.data.entity.TransactionEntity
import com.pockettrack.databinding.ItemTransactionBinding
import com.pockettrack.R
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class TransactionsAdapter(
    private val onClick: (TransactionEntity) -> Unit,
    private val onDelete: (TransactionEntity) -> Unit
) : ListAdapter<TransactionEntity, TransactionsAdapter.VH>(DIFF) {

    companion object {
        private val DIFF = object : DiffUtil.ItemCallback<TransactionEntity>() {
            override fun areItemsTheSame(oldItem: TransactionEntity, newItem: TransactionEntity) = oldItem.id == newItem.id
            override fun areContentsTheSame(oldItem: TransactionEntity, newItem: TransactionEntity) = oldItem == newItem
        }
    }

    inner class VH(val binding: ItemTransactionBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val binding = ItemTransactionBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return VH(binding)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val item = getItem(position)
        holder.binding.root.setOnClickListener { onClick(item) }
        holder.binding.btnDelete.setOnClickListener { onDelete(item) }
        holder.binding.txtCategory.text = item.category
        holder.binding.txtAmount.text = (if (item.type == "income") "+" else "-") + String.format(Locale.getDefault(), "%.2f", item.amount)
        holder.binding.txtNote.text = item.note ?: ""
        val df = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        holder.binding.txtDate.text = df.format(Date(item.date))
    }
}