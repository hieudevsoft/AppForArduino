package com.devapp.appforarduino.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.fragment.app.Fragment
import androidx.viewpager2.widget.ViewPager2
import com.devapp.appforarduino.R
import com.devapp.appforarduino.ui.adapter.ViewPagerAdapter
import com.devapp.appforarduino.util.Util

class LandingPageFragment : Fragment() {
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPager2: ViewPager2
    private lateinit var layoutPagePosition:LinearLayout
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fagment_landing_page, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewPagerAdapter = ViewPagerAdapter()
        viewPager2=view.findViewById(R.id.viewPager2)
        layoutPagePosition=view.findViewById(R.id.page_dot)
        viewPager2.adapter = viewPagerAdapter
        setupPagePosition()
    }

    private fun setupPagePosition() {
            Util.addDot(requireContext(),layoutPagePosition,0)
            viewPager2.registerOnPageChangeCallback(object: ViewPager2.OnPageChangeCallback() {
                override fun onPageSelected(position: Int) {
                    super.onPageSelected(position)
                    Util.addDot(requireContext(),layoutPagePosition,position)
                }
            })
    }

}
