package com.example.userpagetablayout.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

//User model class
@Parcelize
class User(val uid: String, val username: String, val profileImageUrl: String) : Parcelable {
    constructor() : this("", "", "")
}