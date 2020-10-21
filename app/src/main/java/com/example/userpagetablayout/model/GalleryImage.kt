package com.example.userpagetablayout.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//Gallery Image Model class
@Parcelize
class GalleryImage(val id:String, val imageUrl: String) : Parcelable {
    constructor() : this("","")
}