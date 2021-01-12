package com.example.userpagetablayout.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//Video Model class
@Parcelize
class Video(val id: String, val imageUrl: String, val videoUrl: String) : Parcelable {
    constructor() : this("", "", "")
}