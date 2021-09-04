package com.devapp.appforarduino.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.devapp.appforarduino.databinding.LayoutButtonGridBinding
import com.devapp.appforarduino.data.model.PixelData

class LaunchPadAdapter(val heightItem:Int): RecyclerView.Adapter<LaunchPadAdapter.ViewHolder>() {
    var listPixel = emptyList<PixelData>()
    private val map = hashMapOf<Int, ViewHolder>()
    inner class ViewHolder(val binding:LayoutButtonGridBinding):RecyclerView.ViewHolder(binding.root){
        fun bind(pixelData: PixelData){
                binding.root.setCardBackgroundColor(Color.parseColor(pixelData.color))
                binding.root.layoutParams.height = heightItem
                binding.root.requestLayout()
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutButtonGridBinding.inflate(LayoutInflater.from(parent.context),parent,false))
    }


    fun setData(list:List<PixelData>?){
        val diffUtil = com.devapp.appforarduino.util.DiffUtil(listPixel, list)
        val diffUtilResult = DiffUtil.calculateDiff(diffUtil,true)
        if (list != null) {
            listPixel = list
        }
        diffUtilResult.dispatchUpdatesTo(this)
    }
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        map[position] = holder
        holder.bind(listPixel[position])
        holder.binding.root.setOnLongClickListener {
            onItemLongClickListener?.let { it(position) }
            true
        }
    }

    fun setColorForPixel(holder: ViewHolder, color:String){
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