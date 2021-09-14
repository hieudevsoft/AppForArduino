package com.devapp.appforarduino.ui.fragments


import android.app.Activity.RESULT_OK
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.devapp.appforarduino.R
import com.devapp.appforarduino.databinding.FragmentImageBinding
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.material.snackbar.Snackbar
import com.yalantis.ucrop.UCrop
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
		private var show = true
		private val TAG = "ImageFragment"
		private lateinit var _binding: FragmentImageBinding
		private val binding get() = _binding!!
		private lateinit var bitmap: Bitmap
		private lateinit var bitmapScaled: Bitmap
		override fun onCreateView(
				inflater: LayoutInflater,
				container: ViewGroup?,
				savedInstanceState: Bundle?
		): View? {
				_binding = FragmentImageBinding.inflate(inflater)
				return binding.root
		}

		override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
				showHidePreviewLed(show)
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
						show=!show
						showHidePreviewLed(show)
				}

				super.onViewCreated(view, savedInstanceState)
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
										bitmapScaled = getResizedBitmap(bitmap, WIDHT_EXPECTED, HEIGHT_EXPECTED)!!
										binding.imgViewPreview.setImageBitmap(bitmapScaled)
										binding.imgViewPreview.drawable.isFilterBitmap = false
										Log.d(TAG, "on Bitmap Scaled Cropped: ${bitmapScaled?.height} ${bitmapScaled?.width} " +
													"${bitmapScaled?.density}")
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
						binding.imgViewPreview.setImageBitmap(bitmap)
						val tempFile = File(requireActivity().cacheDir, fileOriginalTemp)
						val out = FileOutputStream(tempFile)
						val result = bitmap.compress(Bitmap.CompressFormat.PNG, 100, out)
						Log.d(TAG, "onActivityResult: save cache: $result")
						out.flush()
						out.close()
						val bitmapScaled = getResizedBitmap(bitmap, WIDHT_EXPECTED, HEIGHT_EXPECTED)
						binding.imgView.setImageURI(data?.data)
						binding.imgViewPreview.setImageBitmap(bitmapScaled)
						binding.imgViewPreview.drawable.isFilterBitmap = false
						binding.imgView.viewTreeObserver.addOnPreDrawListener(object :
								ViewTreeObserver.OnPreDrawListener {
								override fun onPreDraw(): Boolean {
										binding.imgView.getViewTreeObserver().removeOnPreDrawListener(this)
										binding.imgView.requestLayout()
										val width = binding.imgView.width
										binding.imgView.layoutParams.height = width / 2
										return true
								}
						})
						binding.imgViewPreview.viewTreeObserver.addOnPreDrawListener(object :
								ViewTreeObserver.OnPreDrawListener {
								override fun onPreDraw(): Boolean {
										binding.imgViewPreview.getViewTreeObserver().removeOnPreDrawListener(this)
										binding.imgViewPreview.requestLayout()
										val width = binding.imgViewPreview.width
										binding.imgViewPreview.layoutParams.height = width / 2
										return true
								}
						})
				} else if (resultCode == ImagePicker.RESULT_ERROR) {
						Snackbar.make(binding.root, ImagePicker.getError(data), Snackbar.LENGTH_LONG)
								.setActionTextColor(Color.RED).show()
				} else if(requestCode!= RC_IMAGE_CROP){
						Snackbar.make(binding.root, "Hủy", Snackbar.LENGTH_LONG)
								.setActionTextColor(Color.RED).show()
				}

		}

		private fun showHidePreviewLed(show:Boolean){
				if(show) {
						Log.d(TAG, "showHidePreviewLed: VISIBLE")
						binding.imgViewPreview.visibility = View.VISIBLE

				} else {
						Log.d(TAG, "showHidePreviewLed: GONE")
						binding.imgViewPreview.visibility = View.GONE
				}
		}

		fun getResizedBitmap(bm: Bitmap, newWidth: Int, newHeight: Int): Bitmap? {
				val width = bm.width
				val height = bm.height
				val scaleWidth = newWidth.toFloat() / width
				val scaleHeight = newHeight.toFloat() / height
				val matrix = Matrix()
				matrix.postScale(scaleWidth, scaleHeight)
				val resizedBitmap = Bitmap.createBitmap(
						bm, 0, 0, width, height, matrix, false
				)
				bm.recycle()
				return resizedBitmap
		}

}