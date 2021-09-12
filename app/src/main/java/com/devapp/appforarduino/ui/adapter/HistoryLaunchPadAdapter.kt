package com.devapp.appforarduino.ui.adapter


import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.devapp.appforarduino.data.model.PixelDataTable
import com.devapp.appforarduino.databinding.ItemHistoryLaunchpadBinding


class HistoryLaunchPadAdapter : RecyclerView.Adapter<HistoryLaunchPadAdapter.ViewHolder>() {

    inner class ViewHolder(val binding: ItemHistoryLaunchpadBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(pixelDataTable: PixelDataTable){
            binding.data = pixelDataTable
            binding.executePendingBindings()
        }
    }

    private val diffUtilItemCallBack = object:DiffUtil.ItemCallback<PixelDataTable>(){

        override fun areItemsTheSame(oldItem: PixelDataTable, newItem: PixelDataTable): Boolean {
            return oldItem.id === newItem.id
        }

        override fun areContentsTheSame(oldItem: PixelDataTable, newItem: PixelDataTable): Boolean {
            return oldItem.hashCode() == newItem.hashCode()
        }

    }

    private val differ = AsyncListDiffer(this,diffUtilItemCallBack)

    fun submitList(list:List<PixelDataTable>){
        differ.submitList(list)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(ItemHistoryLaunchpadBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    fun getItemAtPosition(position:Int) = differ.currentList[position]

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item)
        holder.binding.root.setOnClickListener {
            onLickItemListener?.let { it(item) }
        }
    }

    private var onLickItemListener:((PixelDataTable)->Unit)?=null

    fun setOnItemClickListener(listener:(PixelDataTable)->Unit){
        onLickItemListener = listener
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }
}