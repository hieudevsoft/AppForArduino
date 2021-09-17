package com.devapp.appforarduino.util

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.widget.ImageView

object DrawableHelper {
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
		fun getBitmap(imageView:ImageView) = (imageView.drawable as BitmapDrawable).bitmap
}