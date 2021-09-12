package com.devapp.appforarduino.data.model

import android.os.Parcel
import android.os.Parcelable

data class PixelData (var color:String?="#000000",var isBrush:Boolean?=false):Parcelable{
		constructor(parcel: Parcel) : this(
				parcel.readString(),
				parcel.readValue(Boolean::class.java.classLoader) as? Boolean
		) {
		}

		override fun writeToParcel(parcel: Parcel, flags: Int) {
				parcel.writeString(color)
				parcel.writeValue(isBrush)
		}

		override fun describeContents(): Int {
				return 0
		}

		companion object CREATOR : Parcelable.Creator<PixelData> {
				override fun createFromParcel(parcel: Parcel): PixelData {
						return PixelData(parcel)
				}

				override fun newArray(size: Int): Array<PixelData?> {
						return arrayOfNulls(size)
				}
		}
}
