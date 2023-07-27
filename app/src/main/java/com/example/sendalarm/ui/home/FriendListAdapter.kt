package com.example.sendalarm.ui.home

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.sendalarm.data.entity.FriendList
import com.example.sendalarm.databinding.ItemFriendsBinding

class FriendListAdapter :
    RecyclerView.Adapter<FriendListAdapter.ViewHolder>() {

    private val items = arrayListOf<FriendList>()

    @SuppressLint("NotifyDataSetChanged")
    fun sendItem(it: List<FriendList>) {
        items.clear()
        items.addAll(it)
        notifyDataSetChanged()
    }

    interface ClickInterface {
        fun onMemberClicked(friendUid: String)
    }

    private lateinit var itemClickListener: ClickInterface
    fun setItemClickListener(myItemClickListener: ClickInterface) {
        itemClickListener = myItemClickListener
    }


    override fun onCreateViewHolder(viewGroup: ViewGroup, viewType: Int): ViewHolder {
        val binding: ItemFriendsBinding =
            ItemFriendsBinding.inflate(
                LayoutInflater.from(viewGroup.context),
                viewGroup,
                false
            )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.apply {
            bind(items[position])

            binding.sendRecordIv.setOnClickListener {
                itemClickListener.onMemberClicked(items[position].friendUid)
            }
        }
    }

    override fun getItemCount(): Int = items.size

    inner class ViewHolder(val binding: ItemFriendsBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(item: FriendList) {
            binding.usernameTv.text = item.friendName
            binding.useremailTv.text = item.email
        }
    }
}