package com.devapp.appforarduino.ui.activities

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.recyclerview.widget.GridLayoutManager
import com.devapp.appforarduino.databinding.ActivityMainBinding
import android.view.ViewTreeObserver
import com.afollestad.dragselectrecyclerview.DragSelectReceiver
import com.afollestad.dragselectrecyclerview.DragSelectTouchListener
import com.afollestad.dragselectrecyclerview.Mode
import com.devapp.appforarduino.LaunchPadAdapter
import com.devapp.appforarduino.data.model.PixelData


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var launchPadAdapter: LaunchPadAdapter
    private val column = 32
    private val row = 64
    private val colorSelected = "#ff77a9"
    private val colorUnSelected = "#ffffff"
    private lateinit var myReceiver: MyReceiver
    private lateinit var listPixel:List<PixelData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setup()
        setContentView(binding.root)

    }

    fun setup(){
        binding.recyclerView.layoutManager = GridLayoutManager(this,column)
        binding.recyclerView.viewTreeObserver
            .addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    Log.d("TAG", "onPreDraw: onPreDraw")
                    if (binding.recyclerView.viewTreeObserver.isAlive)
                        binding.recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                    val width = binding.recyclerView.width.toFloat() / column
                    val height = binding.recyclerView.height.toFloat() / row-1.9
                    listPixel = getListInjectData(column,row)
                    launchPadAdapter = LaunchPadAdapter(height.toInt())
                    binding.recyclerView.adapter = launchPadAdapter
                    launchPadAdapter.setData(listPixel)
                    myReceiver = MyReceiver(listPixel,launchPadAdapter,colorSelected,colorUnSelected)
                    val touchListener = DragSelectTouchListener.create(this@MainActivity,myReceiver){
                        disableAutoScroll()
                        mode = Mode.PATH
                    }
                    binding.recyclerView.addOnItemTouchListener(touchListener)
                    launchPadAdapter.setOnLongClickListener {
                        myReceiver.isAlreadyDraw = listPixel[it].isBrush==true
                        touchListener.setIsActive(true,it)
                    }
                    return true
                }
            })
    }
    class MyReceiver(private var selectedItems:List<PixelData>,
                     private val launchPadAdapter: LaunchPadAdapter,
                     private val colorSelected:String,
                     private val colorUnselected:String,
    ): DragSelectReceiver {
        var isAlreadyDraw = false
        override fun getItemCount(): Int {
            return selectedItems.filter { it.isBrush!! }.size
        }

        override fun isIndexSelectable(index: Int): Boolean {
            return true
        }

        override fun isSelected(index: Int): Boolean {
            return false
        }

        override fun setSelected(index: Int, selected: Boolean) {
            if (selected) {
                Log.d("TAG", "setSelected: $isAlreadyDraw")
                if (!isAlreadyDraw) {
                    val holder = launchPadAdapter.getViewHolderAtPosition(index)
                    if (holder != null) {
                        launchPadAdapter.setColorFotPixel(holder, colorSelected)
                    }
                    selectedItems[index].color = colorSelected
                    selectedItems[index].isBrush = true
                    launchPadAdapter.setData(selectedItems)
                } else {
                    val holder = launchPadAdapter.getViewHolderAtPosition(index)
                    if (holder != null) {
                        launchPadAdapter.setColorFotPixel(holder, colorUnselected)
                    }
                    selectedItems[index].color = colorUnselected
                    selectedItems[index].isBrush = false
                    launchPadAdapter.setData(selectedItems)
                }
                Log.d("TAG", "setSelected: ${selectedItems.filter { it.isBrush!! }}")
            }
        }
    }
}

fun getListInjectData(column:Int,row:Int):List<PixelData>{
    val list = mutableListOf<PixelData>()
    for(i in 0 until column*row) list.add(PixelData())
    return list
}


