package com.devapp.appforarduino.ui.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.airbnb.lottie.LottieAnimationView
import com.devapp.appforarduino.R
import com.devapp.appforarduino.util.Util

class ViewPagerAdapter : RecyclerView.Adapter<PagerVH>() {

    private val sloganText = listOf(
        Util.SLOGAN_PAGE_1, Util.SLOGAN_PAGE_2, Util.SLOGAN_PAGE_3
    )
    private val sloganAnim = listOf(
        R.raw.rocket, R.raw.mobile_technology, R.raw.settings
    )

    private val backGroundSlogan = listOf(
        R.color.background_slogan_1,R.color.background_slogan_2,R.color.background_slogan_3
    )


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PagerVH =
        PagerVH(LayoutInflater.from(parent.context).inflate(R.layout.layout_item_landing_page, parent, false))

    override fun getItemCount(): Int = sloganText.size

    override fun onBindViewHolder(holder: PagerVH, position: Int){
        val itemSloganText = sloganText[position]
        val itemSloganAnim = sloganAnim[position]
        val itemBackGround = backGroundSlogan[position]

        holder.itemView.findViewById<TextView>(R.id.page_text).text = itemSloganText
        if(position==1){
            holder.itemView.findViewById<TextView>(R.id.page_text).setTextColor(Color.WHITE)
        }
        holder.itemView.findViewById<LottieAnimationView>(R.id.lottie).setAnimation(itemSloganAnim)
        holder.itemView.findViewById<ConstraintLayout>(R.id.rootLayout).setBackgroundResource(itemBackGround)
    }
}

class PagerVH(itemView: View) : RecyclerView.ViewHolder(itemView)