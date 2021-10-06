package com.devapp.appforarduino.ui.fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.SharedPreferences
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.os.Bundle
import android.preference.Preference
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.devapp.appforarduino.R
import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.databinding.FragmentTextBinding
import com.devapp.appforarduino.ui.activities.HomeActivity
import com.devapp.appforarduino.ui.viewmodels.HomeViewModel
import com.devapp.appforarduino.util.ColorSheetSingleTon
import com.devapp.appforarduino.util.Util
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

class TextFragment : Fragment(R.layout.fragment_text) {
		private lateinit var preference:SharedPreferences
		private var _binding: FragmentTextBinding? = null
		private lateinit var model: HomeViewModel
		private lateinit var connectivityManager: ConnectivityManager
		private val binding get() = _binding?.let { it }!!
		override fun onCreateView(
				inflater: LayoutInflater,
				container: ViewGroup?,
				savedInstanceState: Bundle?
		): View? {
				_binding = FragmentTextBinding.inflate(inflater)
				model = (activity as HomeActivity).homeViewModel
				connectivityManager =
						requireContext().getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
				return binding.root
		}

		override fun onResume() {
				connectivityManager.registerDefaultNetworkCallback(object :
						ConnectivityManager.NetworkCallback() {
						override fun onAvailable(network: Network) {
								requireActivity().runOnUiThread {
										showOrHideNoInternet(false)
								}
								super.onAvailable(network)
						}

						override fun onLost(network: Network) {
								requireActivity().runOnUiThread {
										showOrHideNoInternet(true)
								}
								super.onLost(network)
						}

						override fun onUnavailable() {
								requireActivity().runOnUiThread {
										showOrHideNoInternet(true)
										Snackbar.make(binding.root, "Không hỗ trợ internet", Snackbar.LENGTH_LONG)
												.show()
								}
								super.onUnavailable()
						}
				})
				super.onResume()
		}

		private fun showOrHideNoInternet(isShow: Boolean) {
				binding.lyContent.visibility = if (isShow) View.GONE else View.VISIBLE
				binding.lyNoInternetConnect.visibility = if (isShow) View.VISIBLE else View.GONE
		}


		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
				super.onViewCreated(view, savedInstanceState)
				preference = requireContext().getSharedPreferences("app", MODE_PRIVATE)
				subscribesObserver()
				binding.btnChooseColor.setOnClickListener {
						openBottomSheetColor()
				}
				binding.btnPush.setOnClickListener {
						val intColor = binding.edtText.currentTextColor
						val textData = TextData(
								Util.validDataText(binding.edtText.text.toString()),
								String.format(
										"#%06X", (0xFFFFFF and intColor)
								)
						)
						model.updateTextAndColorToFireBase(textData)
						lifecycleScope.launchWhenStarted {
								model.updateState.collect {
										when (it) {
												is HomeViewModel.UpdateState.Loading -> {
														binding.loadingDots.visibility = View.VISIBLE
												}
												is HomeViewModel.UpdateState.Error -> {
														binding.loadingDots.visibility = View.GONE
														Snackbar.make(binding.root, it.message, Snackbar.LENGTH_LONG)
																.setActionTextColor(Color.RED)
																.show()
												}
												is HomeViewModel.UpdateState.Success -> {
														binding.loadingDots.visibility = View.GONE
														model.updateOptionToFireBase(1)
														FirebaseDatabase.getInstance().reference.child("reset").setValue(2)
														Snackbar.make(
																binding.root,
																"Cập nhật chữ thành công",
																Snackbar.LENGTH_LONG
														)
																.setActionTextColor(Color.RED)
																.show()
														model.saveTextAndColor(textData)
												}
												else -> {

												}
										}
								}
						}
				}
				binding.textRunning.isChecked = preference.getBoolean("state_text_run",false)
				binding.textRunning.setOnCheckedChangeListener { _, isChecked ->
						binding.loadingDots.visibility = View.VISIBLE
						try {
								lifecycleScope.launchWhenResumed {
										if(isChecked){
												FirebaseDatabase.getInstance().reference.child("textRun").setValue(1).await()
												preference.edit().putBoolean("state_text_run",true).apply()
										}else{
												FirebaseDatabase.getInstance().reference.child("textRun").setValue(0).await()
												preference.edit().putBoolean("state_text_run",false).apply()
										}
										withContext(Dispatchers.Main){
												binding.loadingDots.visibility = View.GONE
										}
								}
						} catch (e: Exception) {
								binding.loadingDots.visibility = View.GONE
								Toast.makeText(requireContext(), "Vui lòng thử lại ", Toast.LENGTH_SHORT).show()
						}

				}
		}

		private fun subscribesObserver() {
				model.stateTextEditText.observe(viewLifecycleOwner, {
						it?.let {
								binding.edtText.setText(it[0])
								it[1]?.let { it1 -> binding.edtText.setTextColor(it1.toInt()) }
						}
				})

		}

		private fun openBottomSheetColor() {
				val colorSheetSingleTon = ColorSheetSingleTon.getInstance()
				colorSheetSingleTon.setListener(object : ColorSheetSingleTon.ColorSheetListener {
						override fun onColorSelected(color: Int) {
								binding.edtText.setTextColor(color)

						}
				})
				colorSheetSingleTon.createColorSheet(resources.getIntArray(R.array.ColorSource))
				colorSheetSingleTon.show(childFragmentManager)
		}

		override fun onDestroyView() {
				try {
						model.setEmptyForUpdateState()
						model.setStateText(
								hashMapOf(
										0 to binding.edtText.text.toString(),
										1 to binding.edtText.currentTextColor.toString()
								)
						)
						_binding = null
						model.setEmptyForUpdateState()
				} catch (e: Exception) {

				}

				super.onDestroyView()
		}
}