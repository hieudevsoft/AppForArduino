package com.devapp.appforarduino.ui.activities

import android.os.Bundle
import android.util.Log
import android.view.ViewTreeObserver
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.afollestad.dragselectrecyclerview.DragSelectReceiver
import com.afollestad.dragselectrecyclerview.DragSelectTouchListener
import com.afollestad.dragselectrecyclerview.Mode
import com.devapp.appforarduino.R
import com.devapp.appforarduino.data.model.PixelData
import com.devapp.appforarduino.databinding.ActivityLaunchPadBinding
import com.devapp.appforarduino.ui.adapter.LaunchPadAdapter
import com.devapp.appforarduino.util.ColorSheetSingleTon
import com.devapp.appforarduino.util.Util


class LaunchPadActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLaunchPadBinding
    private lateinit var launchPadAdapter: LaunchPadAdapter
    private val column = 32
    private val row = 64
    private var colorSelected = "#ff77a9"
    private val colorUnSelected = "#000000"
    private lateinit var myReceiver: MyReceiver
    private lateinit var listPixel: List<PixelData>
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLaunchPadBinding.inflate(layoutInflater)
        setup()
        setContentView(binding.root)
        binding.btnChooseColor.setOnClickListener {
            openBottomSheetColor()
        }
        binding.btnPush.setOnClickListener {
            Log.d(
                "Test Pixel Data", "onCreate: ${
                    Util.covertListColorToRgbArray(launchPadAdapter.listPixel)
                }"
            )
        }
    }

    private fun openBottomSheetColor() {
        val colorSheetSingleTon = ColorSheetSingleTon.getInstance(this)
        colorSheetSingleTon.setListener(object : ColorSheetSingleTon.ColorSheetListener {
            override fun onColorSelected(color: Int) {
                myReceiver.colorSelected = "#" + Integer.toHexString(color)
                Log.d("TEST", "onColorSelected: ${color and 0x00ffffff}")
            }
        })
        colorSheetSingleTon.createColorSheet(resources.getIntArray(R.array.ColorSource))
        colorSheetSingleTon.show(supportFragmentManager)
    }

    fun setup() {
        binding.recyclerView.layoutManager = GridLayoutManager(this, column)
        binding.recyclerView.viewTreeObserver
            .addOnPreDrawListener(object : ViewTreeObserver.OnPreDrawListener {
                override fun onPreDraw(): Boolean {
                    Log.d("TAG", "onPreDraw: onPreDraw")
                    if (binding.recyclerView.viewTreeObserver.isAlive)
                        binding.recyclerView.viewTreeObserver.removeOnPreDrawListener(this)
                    val width = binding.recyclerView.width.toFloat() / column
                    val height =
                        (binding.recyclerView.height.toFloat() - binding.lyControl.measuredHeight + 50) / row
                    listPixel = getListInjectData(column,row)
                    launchPadAdapter = LaunchPadAdapter(height.toInt())
                    binding.recyclerView.adapter = launchPadAdapter
                    launchPadAdapter.setData(listPixel)
                    myReceiver = MyReceiver(listPixel,launchPadAdapter,colorSelected,colorUnSelected)
                    val touchListener =
                        DragSelectTouchListener.create(this@LaunchPadActivity, myReceiver) {
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
    class MyReceiver(
        private var selectedItems: List<PixelData>,
        private val launchPadAdapter: LaunchPadAdapter,
        var colorSelected: String,
        private val colorUnselected: String,
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
                        launchPadAdapter.setColorForPixel(holder, colorSelected)
                    }
                    selectedItems[index].color = colorSelected
                    selectedItems[index].isBrush = true
                    launchPadAdapter.setData(selectedItems)
                } else {
                    val holder = launchPadAdapter.getViewHolderAtPosition(index)
                    if (holder != null) {
                        launchPadAdapter.setColorForPixel(holder, colorUnselected)
                    }
                    selectedItems[index].color = colorUnselected
                    selectedItems[index].isBrush = false
                    launchPadAdapter.setData(selectedItems)
                }
            }
        }
    }
}

fun getListInjectData(column:Int,row:Int):List<PixelData>{
    val list = mutableListOf<PixelData>()
    for(i in 0 until column*row) list.add(PixelData())
    return list
}


