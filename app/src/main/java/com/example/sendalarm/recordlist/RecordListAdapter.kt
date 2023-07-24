package com.example.sendalarm.recordlist

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sendalarm.data.entity.SendRecord
import com.example.sendalarm.databinding.ItemRecordListBinding

class RecordListAdapter :
    RecyclerView.Adapter<RecordListAdapter.ViewHolder>() {

    private val items = arrayListOf<SendRecord>()

    @SuppressLint("NotifyDataSetChanged")
    fun sendItem(it: List<SendRecord>) {
        items.clear()
        items.addAll(it)
        notifyDataSetChanged()
    }

    interface ClickInterface {
        fun onRecordClicked()
    }

    private lateinit var itemClickListener: ClickInterface
    fun setItemClickListener(myItemClickListener: ClickInterface) {
        itemClickListener = myItemClickListener
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemRecordListBinding =
            ItemRecordListBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.apply {
            bind(items[position])

            binding.recordPlayIv.setOnClickListener {
                itemClickListener.onRecordClicked()
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemRecordListBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: SendRecord) {
            binding.usernameTv.text = item.receiverName
            binding.titleTv.text = item.title
            binding.sendDateTv.text = item.time.toString()
        }
    }
}