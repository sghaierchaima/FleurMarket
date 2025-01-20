package com.example.monapp.model

import android.os.Parcelable
import com.google.firebase.firestore.PropertyName
import kotlinx.parcelize.Parcelize

@Parcelize
data class Flower(

    val nom: String = "",


    val categorie: String = "",


    var prix: Double = 0.0,


    val description: String = "",

    @PropertyName("imageUrl")
    var imageUrl: String? = null,
    var userId: String = ""
):Parcelable
