package com.example.monapp.model

import com.google.firebase.firestore.PropertyName

data class Flower(

    val nom: String = "",


    val categorie: String = "",


    var prix: Double = 0.0,


    val description: String = "",

    @PropertyName("imageUrl")
    var imageUrl: String? = null,
    var userId: String = ""
)
