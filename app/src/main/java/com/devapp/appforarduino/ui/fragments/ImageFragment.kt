package com.devapp.appforarduino.ui.fragments


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.navArgs
import com.devapp.appforarduino.R
import com.devapp.appforarduino.data.model.ImageData
import com.devapp.appforarduino.databinding.FragmentImageBinding
import com.devapp.appforarduino.ui.activities.HomeActivity
import com.devapp.appforarduino.ui.viewmodels.HomeViewModel
import com.devapp.appforarduino.ui.viewmodels.LaunchPadViewModel
import com.devapp.appforarduino.util.DrawableHelper
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.yalantis.ucrop.UCrop
import kotlinx.coroutines.flow.collect
import java.io.File
import java.io.FileOutputStream


class ImageFragment : Fragment(R.layout.fragment_image) {
		companion object {
				val MAX_SIZE = 1024
				val MAX_HEIGHT = 520
				val MAX_WIDTH = 520
				val WIDHT_EXPECTED = 64
				val HEIGHT_EXPECTED = 32
				val fileOriginalTemp = "tempFileOriginalImage.png"
				val fileCroppedTemp = "tempFileCroppedImage.png"
				val RC_IMAGE_CROP = 123
		}

		private var show = false
		private val TAG = "ImageFragment"
		private lateinit var _binding: FragmentImageBinding
		private val binding get() = _binding!!
		private lateinit var bitmap: Bitmap
		private lateinit var bitmapScaled: Bitmap
		private lateinit var model: HomeViewModel
		private val args: ImageFragmentArgs by navArgs()
		override fun onCreateView(
				inflater: LayoutInflater,
				container: ViewGroup?,
				savedInstanceState: Bundle?
		): View? {
				_binding = FragmentImageBinding.inflate(inflater)
				model = (requireActivity() as HomeActivity).homeViewModel
				return binding.root
		}

		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
				val myImageData = args.imageData
				myImageData?.let {
						binding.imgView.setImageBitmap(it.data)
						saveToCacheImageFile(DrawableHelper.getBitmap(binding.imgView))
						val bitmapTemp = it.data.copy(Bitmap.Config.ARGB_8888, false)
						bitmapScaled = DrawableHelper.getResizedBitmap(
								bitmapTemp, WIDHT_EXPECTED,
								HEIGHT_EXPECTED
						)!!
						binding.imgViewPreview.setImageBitmap(bitmapScaled)
						binding.imgViewPreview.drawable.isFilterBitmap = false

				}
				showOrHidePreviewImage(show)
				binding.btnChooseGalerry.setOnClickListener {
						ImagePicker.with(this)
								.galleryOnly()
								.galleryMimeTypes(
										arrayOf(
												"image/png",
												"image/jpg",
												"image/jpeg"
										)
								)
								.compress(MAX_SIZE)
								.maxResultSize(MAX_WIDTH, MAX_HEIGHT)
								.start()
				}
				binding.btnChooseCamera.setOnClickListener {
						ImagePicker.with(this)
								.cameraOnly()
								.compress(MAX_SIZE)
								.maxResultSize(MAX_WIDTH, MAX_HEIGHT)
								.start()
				}

				binding.btnChooseCrop.setOnClickListener {
						processCropImage()
				}
				binding.btnChoosePreview.setOnClickListener {
						show = !show
						showOrHidePreviewImage(show)
				}

				binding.btnSaveLocal.setOnClickListener {
						lifecycleScope.launchWhenResumed {
								val bitmapData = (binding.imgView.drawable as BitmapDrawable).bitmap
								val stateSave = model.saveImage(ImageData(bitmapData))
								if (stateSave >= 0) {
										Snackbar.make(binding.root, "Bạn đã lưu lại thành công ", Snackbar.LENGTH_LONG)
												.show()
								} else {
										Snackbar.make(
												binding.root, "Bạn đã lưu lại không thành công ", Snackbar
														.LENGTH_LONG
										).setActionTextColor(Color.RED)
												.show()
								}

						}
				}

				subscribersObserver()

				binding.btnPushFireBase.setOnClickListener {
						val bitmapPush = (binding.imgViewPreview.drawable as BitmapDrawable).bitmap
						model.updateImageToFireBase(bitmapPush)
				}

				super.onViewCreated(view, savedInstanceState)
		}

		private fun subscribersObserver() {
				lifecycleScope.launchWhenStarted {
						model.updateImageState.collect {
								when (it) {
										is HomeViewModel.UpdateState.Loading -> {
												binding.loadingDots.visibility = View.VISIBLE
										}
										is HomeViewModel.UpdateState.Error -> {
												binding.loadingDots.visibility = View.GONE
												Snackbar.make(binding.root, it.message, Snackbar.LENGTH_LONG)

														.show()
										}
										is HomeViewModel.UpdateState.Success -> {
												binding.loadingDots.visibility = View.GONE
												try {
														model.updateOptionToFireBase(2)
														Snackbar.make(
																binding.root,
																"Cập hình ảnh thành công",
																Snackbar.LENGTH_LONG
														)
																.setActionTextColor(Color.RED)
																.show()
												} catch (e: Exception) {
														Snackbar.make(
																binding.root,
																"Có lỗi xảy ra khi cập nhật ~",
																Snackbar.LENGTH_LONG
														).setActionTextColor(Color.RED)
																.show()
												}
										}
										else -> {

										}
								}
						}
				}
		}


		private fun processCropImage() {
				try {
						bitmap?.let {
								val fileTempOriginal = File(requireActivity().cacheDir, fileOriginalTemp)
								val fileTempCropped = File(requireActivity().cacheDir, fileCroppedTemp)
								val sourceUri = Uri.fromFile(fileTempOriginal)
								val sourceDestination = Uri.fromFile(fileTempCropped)
								UCrop.of(sourceUri, sourceDestination)
										.start(requireContext(), this, RC_IMAGE_CROP)
						}

				} catch (e: Exception) {
						Toast.makeText(
								requireContext(), "Có lỗi xảy ra ! Vui lòng thử lại ", Toast
										.LENGTH_SHORT
						).show()
				}
		}

		override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
				super.onActivityResult(requestCode, resultCode, data)
				if (resultCode == RESULT_OK && requestCode == RC_IMAGE_CROP) {
						val resultUri = data?.let { UCrop.getOutput(it) }
						binding.imgView.setImageURI(resultUri)
						if (resultUri != null) {
								bitmap = BitmapFactory.decodeFile(File(resultUri.path).path)
								bitmap?.let {
										bitmapScaled =
												DrawableHelper.getResizedBitmap(bitmap, WIDHT_EXPECTED, HEIGHT_EXPECTED)!!
										binding.imgViewPreview.setImageBitmap(bitmapScaled)
										binding.imgViewPreview.drawable.isFilterBitmap = false
										Log.d(
												TAG,
												"on Bitmap Scaled Cropped: ${bitmapScaled?.height} ${bitmapScaled?.width} " +
															"${bitmapScaled?.density}"
										)
								}
						}
				} else if (resultCode == UCrop.RESULT_ERROR) {
						Snackbar.make(binding.root, UCrop.getError(data!!).toString(), Snackbar.LENGTH_LONG)
								.setActionTextColor(Color.RED).show()
				}
				if (resultCode == RESULT_OK && requestCode != RC_IMAGE_CROP) {
						Log.d(TAG, "onActivityResult: IMAGE PICK")
						val file = ImagePicker.getFile(data)
						bitmap = BitmapFactory.decodeFile(file?.path)
						binding.imgView.setImageBitmap(bitmap)
						saveToCacheImageFile(bitmap)
						bitmapScaled = DrawableHelper.getResizedBitmap(
								bitmap.copy(Bitmap.Config.ARGB_8888, false),
								WIDHT_EXPECTED,
								HEIGHT_EXPECTED
						)!!
						binding.imgViewPreview.setImageBitmap(bitmapScaled)
						binding.imgViewPreview.drawable.isFilterBitmap = false
				} else if (resultCode == ImagePicker.RESULT_ERROR) {
						Snackbar.make(binding.root, ImagePicker.getError(data), Snackbar.LENGTH_LONG)
								.setActionTextColor(Color.RED).show()
				} else if (requestCode != RC_IMAGE_CROP) {
						Snackbar.make(binding.root, "Hủy", Snackbar.LENGTH_LONG)
								.setActionTextColor(Color.RED).show()
				}

		}

		private fun saveToCacheImageFile(bitmapResult: Bitmap) {
				val tempFile = File(requireActivity().cacheDir, fileOriginalTemp)
				val out = FileOutputStream(tempFile)
				val result = bitmapResult.compress(Bitmap.CompressFormat.PNG, 100, out)
				Log.d(TAG, "onActivityResult: save cache: $result")
				out.flush()
				out.close()
		}

		private fun showOrHidePreviewImage(show: Boolean) {
				if (show) {
						Log.d(TAG, "showHidePreviewLed: VISIBLE")
						binding.imgViewPreview.visibility = View.VISIBLE

				} else {
						Log.d(TAG, "showHidePreviewLed: GONE")
						binding.imgViewPreview.visibility = View.GONE
				}
		}

		override fun onStart() {
				setupPreDraw()
				model.stateImageAndPreview.observe(viewLifecycleOwner, {
						try {
								bitmap = it[0] as Bitmap
								bitmapScaled = DrawableHelper.getResizedBitmap(
										bitmap.copy(Bitmap.Config.ARGB_8888, false),
										WIDHT_EXPECTED,
										HEIGHT_EXPECTED
								)!!
								show = it[1] as Boolean
								showOrHidePreviewImage(show)
								binding.imgView.setImageBitmap(bitmap)
								binding.imgViewPreview.setImageBitmap(bitmapScaled)
								binding.imgViewPreview.drawable.isFilterBitmap = false
						} catch (e: Exception) {
								e.printStackTrace()
						}

				})
				super.onStart()
		}

		fun setupPreDraw() {
				binding.imgView.viewTreeObserver.addOnPreDrawListener(object :
						ViewTreeObserver.OnPreDrawListener {
						override fun onPreDraw(): Boolean {
								binding.imgView.getViewTreeObserver().removeOnPreDrawListener(this)
								binding.imgView.requestLayout()
								val width = binding.imgView.width
								binding.imgView.layoutParams.height = width / 2
								binding.imgViewPreview.viewTreeObserver.addOnPreDrawListener(object :
										ViewTreeObserver.OnPreDrawListener {
										override fun onPreDraw(): Boolean {
												binding.imgViewPreview.getViewTreeObserver().removeOnPreDrawListener(this)
												binding.imgViewPreview.requestLayout()
												binding.imgViewPreview.layoutParams.height = width / 2
												return true
										}
								})
								return true
						}
				})
		}

		override fun onDestroyView() {
				try {
						model.setEmptyForUpdateImageState()
						if ((binding.imgView.drawable as BitmapDrawable).bitmap != null
						) {
								model.setStateImageAndPreview(
										hashMapOf(
												0 to (binding.imgView.drawable as BitmapDrawable).bitmap,
												1 to show
										)
								)
						}
				} catch (e: Exception) {

				}
				super.onDestroyView()
		}
}