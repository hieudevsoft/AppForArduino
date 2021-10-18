package com.devapp.appforarduino.ui.fragments

import android.content.Context
import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Color
import android.net.ConnectivityManager
import android.net.Network
import android.os.Build
import android.os.Bundle
import android.preference.Preference
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.devapp.appforarduino.R
import com.devapp.appforarduino.data.model.TextData
import com.devapp.appforarduino.databinding.FragmentTextBinding
import com.devapp.appforarduino.helper.Helper
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

class TextFragment : Fragment(R.layout.fragment_text),View.OnTouchListener {
		companion object {
				const val RC_AUDIO = 0
				val TAG = "TextFragment"
		}
		private lateinit var preference:SharedPreferences
		private var _binding: FragmentTextBinding? = null
		private lateinit var model: HomeViewModel
		private lateinit var connectivityManager: ConnectivityManager
		private lateinit var speechRecognizerIntent: Intent
		private lateinit var speechRecognizer: SpeechRecognizer
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
				binding.cardSpeak.setOnTouchListener(this)
				if (ContextCompat.checkSelfPermission(
								requireContext(),
								android.Manifest.permission.RECORD_AUDIO
						) != PackageManager.PERMISSION_GRANTED
				) {
						checkPermission()
				}else{
						setupSpeechRecognizer()
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

		override fun onDestroy() {
				speechRecognizer.destroy()
				super.onDestroy()
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

		override fun onTouch(v: View?, event: MotionEvent?): Boolean {
				if (v != null) {
						when(v.id){
								binding.cardSpeak.id->{
										if (event?.action == MotionEvent.ACTION_DOWN) {
												Helper.scaleViewPress(v)
												speechRecognizer.startListening(speechRecognizerIntent)
										}
										if (event?.action == MotionEvent.ACTION_UP) {
												binding.imgSpeech.setImageResource(R.drawable.ic_voice_on)
												speechRecognizer.stopListening()
												Helper.scaleViewUp(v)
										}
								}
						}
				}

				return true
		}

		private fun checkPermission() {
				ActivityCompat.requestPermissions(
						requireActivity(),
						arrayOf(android.Manifest.permission.RECORD_AUDIO),
						RC_AUDIO
				)
		}

		private fun setupSpeechRecognizer() {
				speechRecognizerIntent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH)
				speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
				speechRecognizerIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,"en")
				speechRecognizer = SpeechRecognizer.createSpeechRecognizer(requireContext())
				speechRecognizer.setRecognitionListener(object: RecognitionListener {
						override fun onReadyForSpeech(params: Bundle?) {
								Log.d(TAG, "onReadyForSpeech: Ready~")
						}

						override fun onBeginningOfSpeech() {
								Log.d(TAG, "onBeginningOfSpeech: Listening...")
						}

						override fun onRmsChanged(rmsdB: Float) {
								Log.d(TAG, "onRmsChanged: RmsChanged...")
						}

						override fun onBufferReceived(buffer: ByteArray?) {
								Log.d(TAG, "onBufferReceived: BufferReceived...")
						}

						override fun onEndOfSpeech() {
								Log.d(TAG, "onEndOfSpeech: End Of Speech...")
						}

						override fun onError(error: Int) {
								Snackbar.make(binding.root, "Please try again...", Snackbar.LENGTH_LONG)
										.setActionTextColor(Color.RED)
										.show()
								Log.d(TAG, "onError: $error")
						}

						override fun onResults(results: Bundle?) {
								binding.imgSpeech.setImageResource(R.drawable.ic_baseline_keyboard_voice_24)
								val result = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
								if (result != null) {
										if(result.size>1){
												binding.edtText.setText(result[0])
												binding.imgSpeech.setImageResource(R.drawable.ic_baseline_keyboard_voice_24)
										}else Snackbar.make(binding.root,"Không nhận dạng được ",Snackbar
												.LENGTH_LONG).show()
								}
								Log.d(TAG, "onResults: $result")
						}

						override fun onPartialResults(partialResults: Bundle?) {
								Log.d(TAG, "onPartialResults: onPartialResults")
						}

						override fun onEvent(eventType: Int, params: Bundle?) {
								Log.d(TAG, "onEvent: onEvent... $eventType")
						}

				})
		}
		override fun onRequestPermissionsResult(
				requestCode: Int,
				permissions: Array<out String>,
				grantResults: IntArray
		) {
				if (requestCode == RC_AUDIO && grantResults.size > 0) {
						if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
								Toast.makeText(requireContext(), "Permission granted ~", Toast.LENGTH_SHORT).show()
								setupSpeechRecognizer()
						} else {
								Snackbar.make(binding.root, "Bạn cần cung cấp quyền !", Snackbar.LENGTH_LONG)
										.setActionTextColor(Color.RED)
										.show()
						}
				}
				super.onRequestPermissionsResult(requestCode, permissions, grantResults)
		}
}