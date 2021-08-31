package com.devapp.appforarduino

import android.graphics.Color
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.devapp.appforarduino.databinding.LayoutButtonGridBinding

class LaunchPadAdapter(val heightItem:Int): RecyclerView.Adapter<LaunchPadAdapter.ViewHolder>() {
    var listPixel = emptyList<PixelData>()
    private val map = hashMapOf<Int,ViewHolder>()
    inner class ViewHolder(val binding:LayoutButtonGridBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(pixelData: PixelData){
                binding.root.setCardBackgroundColor(Color.parseColor(pixelData.color))
                binding.root.layoutParams.height = heightItem
                binding.root.requestLayout()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): LaunchPadAdapter.ViewHolder {
        return ViewHolder(LayoutButtonGridBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }

    val utilCallBack  = object:DiffUtil.ItemCallback<PixelData>(){
        override fun areItemsTheSame(oldItem: PixelData, newItem: PixelData): Boolean {
            return oldItem.color == oldItem.color && oldItem.isBrush == newItem.isBrush
        }

        override fun areContentsTheSame(oldItem: PixelData, newItem: PixelData): Boolean {
            return oldItem.color == oldItem.color && oldItem.isBrush == newItem.isBrush
        }

    }

    val differ = AsyncListDiffer(this,utilCallBack)

    fun setData(list:List<PixelData>?){
        val diffUtil = DiffUtil(listPixel,list)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil,true)
        if (list != null) {
            listPixel = list
        }
        diffUtilResult.dispatchUpdatesTo(this)
    }
    override fun onBindViewHolder(holder: LaunchPadAdapter.ViewHolder, position: Int) {
        map[position] = holder
        holder.bind(listPixel[position])
        holder.binding.root.setOnLongClickListener {

//            if(listPixel[position].isBrush==false){
//                holder.binding.root.setCardBackgroundColor(Color.parseColor("#ff77a9"))
//            }
//            else {
//                holder.binding.root.setCardBackgroundColor(Color.parseColor("#ffffff"))
//            }
            onItemLongClickListener?.let { it(position) }
            true
        }
    }

    fun setColorFotPixel(holder:ViewHolder,color:String){
        holder.binding.root.setCardBackgroundColor(Color.parseColor(color))
    }

    fun getViewHolderAtPosition(position: Int) = map[position]

    var onItemLongClickListener:((Int)->Unit)?=null
    fun setOnLongClickListener(listener:(Int)->Unit){
        onItemLongClickListener = listener
    }

    override fun getItemCount(): Int {
        return listPixel.size
    }
}