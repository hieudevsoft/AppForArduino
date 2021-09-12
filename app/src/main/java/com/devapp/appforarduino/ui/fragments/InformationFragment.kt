package com.devapp.appforarduino.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.OvershootInterpolator
import androidx.fragment.app.Fragment
import com.devapp.appforarduino.R
import com.devapp.appforarduino.data.model.Member
import com.devapp.appforarduino.databinding.FragmentInformationTeamBinding
import com.devapp.appforarduino.ui.adapter.InformationAdapter
import jp.wasabeef.recyclerview.animators.SlideInLeftAnimator

class InformationFragment : Fragment(R.layout.fragment_information_team) {

		private lateinit var _binding: FragmentInformationTeamBinding
		private val binding get() = _binding!!
		private lateinit var informationAdapter: InformationAdapter
		override fun onCreateView(
				inflater: LayoutInflater,
				container: ViewGroup?,
				savedInstanceState: Bundle?
		): View? {
				_binding = FragmentInformationTeamBinding.inflate(inflater)
				return binding.root
		}

		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
				informationAdapter = InformationAdapter()
				binding.recyclerView.adapter = informationAdapter
				binding.recyclerView.itemAnimator = SlideInLeftAnimator(OvershootInterpolator(1f))
				informationAdapter.submitList(
						listOf(
								Member(
										"tuan hiep",
										" le",
										"designer",
										"ui/ux Design",
										"Nghe An Convince",
										"Viet Nam",
										"+84 " +
													"0123457xx",
										"+84 4456455xx",
										"hiepdesigner@gmail.com",
										"www.UwuTeam.com.vn",
										R.raw.json_card_test,
										R.drawable.img_test
								),
								Member(
										"d.t.anh",
										" nguyen",
										"developer",
										"web developer",
										"Lao Cai Convince",
										"Viet Nam",
										"+84 " +
													"0123567xx",
										"+84 4456455xx",
										"tuananhdev@gmail.com",
										"www.UwuTeam.com.vn",
										R.raw.json_test_1,
										R.drawable.img_test_1
								),
								Member(
										"hieu huy",
										" bui",
										"developer",
										"mobile developer",
										"Bac Giang Convince",
										"Viet Nam",
										"+84" +
													" " +
													"0123456xx",
										"+84 4456455xx",
										"hieubg1307@gmail.com",
										"www.UwuTeam.com.vn",
										R.raw.json_test_2,
										R.drawable.img_test_2
								),

								)
				)
				super.onViewCreated(view, savedInstanceState)
		}
}