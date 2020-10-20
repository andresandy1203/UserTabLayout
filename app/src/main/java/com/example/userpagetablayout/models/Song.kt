package com.example.userpagetablayout.models

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//Song Model class
@Parcelize
class Song (val id:String, val albumUrl: String, val songArtist: String, val songName: String, val webUrl: String) :
    Parcelable {
    constructor() : this(" ", " ", " ", " ", "")
}