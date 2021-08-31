package com.devapp.appforarduino.util

import androidx.recyclerview.widget.DiffUtil
import com.devapp.appforarduino.data.model.PixelData

class DiffUtil(private val oldList:List<PixelData>, private val newList:List<PixelData>?): DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldList.size
    }

    override fun getNewListSize(): Int {
        if (newList != null) {
            return newList.size
        }
        return 0
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition]=== newList?.get(newItemPosition)
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].color== newList?.get(newItemPosition)?.color && oldList[oldItemPosition].isBrush== newList?.get(newItemPosition)?.isBrush
    }
}