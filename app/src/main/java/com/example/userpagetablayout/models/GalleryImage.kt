package com.example.userpagetablayout.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class GalleryImage(val id:String, val imageUrl: String) : Parcelable {
    constructor() : this("","")
}