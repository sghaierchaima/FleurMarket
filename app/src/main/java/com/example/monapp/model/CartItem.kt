package com.example.monapp.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class CartItem(
    val flower: Flower,  // Référence au modèle Flower
    var quantity: Int //quantity ajouter au panier
) : Parcelable
