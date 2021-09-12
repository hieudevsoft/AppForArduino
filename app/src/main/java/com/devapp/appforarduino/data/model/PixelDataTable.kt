package com.devapp.appforarduino.data.model

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.devapp.appforarduino.util.Util

@Entity(tableName = Util.TABLE_PIXEL_AND_COLOR)
class PixelDataTable():Parcelable {
    @PrimaryKey
    lateinit var id:String

    lateinit var data:List<PixelData>

    constructor(parcel: Parcel) : this() {
        id = parcel.readString().toString()
        data = parcel.createTypedArrayList(PixelData)!!
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel?, flags: Int) {
        if (dest != null) {
            dest.writeString(id)
        }
        if (dest != null) {
            dest.writeTypedList(data)
        }
    }

    companion object CREATOR : Parcelable.Creator<PixelDataTable> {
        override fun createFromParcel(parcel: Parcel): PixelDataTable {
            return PixelDataTable(parcel)
        }

        override fun newArray(size: Int): Array<PixelDataTable?> {
            return arrayOfNulls(size)
        }
    }

}