package com.example.userpagetablayout.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
class Video(val id:String, val imageUrl: String, val videoUrl: String) : Parcelable {
    constructor() : this("", "", "")
}