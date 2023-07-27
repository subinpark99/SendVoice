package com.example.sendalarm.ui.setting

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Filter
import android.widget.Filterable
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.sendalarm.R
import com.example.sendalarm.data.entity.User
import com.example.sendalarm.databinding.ItemEmailListBinding

class UserListAdapter :
    RecyclerView.Adapter<UserListAdapter.ViewHolder>(), Filterable {

    private val items = ArrayList<User>()
    private var filterItems = ArrayList<User>()


    var setFilter = ItemFilter()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):
            UserListAdapter.ViewHolder {
        val binding: ItemEmailListBinding = ItemEmailListBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: UserListAdapter.ViewHolder, position: Int) {
        val userList = filterItems[position]
        holder.bind(userList)

        holder.itemView.setOnClickListener {

            holder.isSelected = !holder.isSelected

            if (holder.isSelected) {
                holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.light_gray
                    )
                )
            } else {
                holder.itemView.setBackgroundColor(
                    ContextCompat.getColor(
                        holder.itemView.context,
                        R.color.white
                    )
                )
            }

            itemClickListener.onUserClicked(holder.isSelected, userList.email)
        }
    }

    override fun getFilter(): Filter {
        return setFilter
    }

    inner class ViewHolder(val binding: ItemEmailListBinding) :
        RecyclerView.ViewHolder(binding.root) {

        var isSelected = false

        fun bind(user: User) {
            binding.user = user
        }
    }

    override fun getItemCount(): Int = filterItems.size

    inner class ItemFilter : Filter() {
        override fun performFiltering(charSequence: CharSequence): FilterResults {

            val charString = charSequence.toString()
            val filteredList = ArrayList<User>()
            val filterResults = FilterResults()

            if (charString.trim { it <= ' ' }.isEmpty()) {
                filterResults.values = null

            } else if (charString.trim { it <= ' ' }.length >= 3) {
                for (user in items) {
                    if (user.email.contains(charString)) {
                        filteredList.add(user)
                    }
                }
            }

            filterResults.values = filteredList
            filterResults.count = filteredList.size

            return filterResults
        }

        @SuppressLint("NotifyDataSetChanged")
        override fun publishResults(char: CharSequence?, result: FilterResults?) {

            filterItems.clear()
            filterItems.addAll(result?.values as ArrayList<User>)

            notifyDataSetChanged()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun getUserList(user: List<User>) {
        items.clear()
        items.addAll(user)
        notifyDataSetChanged()
    }

    interface InContentInterface {
        fun onUserClicked(clicked: Boolean, email: String)
    }

    private lateinit var itemClickListener: InContentInterface
    fun setItemClickListener(myItemClickListener: InContentInterface) {
        itemClickListener = myItemClickListener
    }

}