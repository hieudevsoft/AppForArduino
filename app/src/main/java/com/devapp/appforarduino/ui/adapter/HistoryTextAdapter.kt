package com.devapp.appforarduino.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.databinding.ItemHistoryTextBinding


class HistoryTextAdapter : RecyclerView.Adapter<HistoryTextAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHistoryTextBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(textData: TextData){
            binding.data = textData
            binding.executePendingBindings()
        }
    }

    private val diffUtilItemCallBack = object:DiffUtil.ItemCallback<TextData>(){
        override fun areItemsTheSame(oldItem: TextData, newItem: TextData): Boolean {
            return oldItem===newItem
        }

        override fun areContentsTheSame(oldItem: TextData, newItem: TextData): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this,diffUtilItemCallBack)

    fun submitList(list:List<TextData>){
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHistoryTextBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    fun getItemAtPosition(position:Int) = differ.currentList[position]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
        holder.binding.root.setOnClickListener {
            onLickItemListener?.let { it(item) }
        }
    }

    private var onLickItemListener:((TextData)->Unit)?=null

    fun setOnItemClickListener(listener:(TextData)->Unit){
        onLickItemListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}