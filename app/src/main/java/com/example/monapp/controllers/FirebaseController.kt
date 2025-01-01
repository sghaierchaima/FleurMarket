package com.example.monapp.controllers



import android.net.Uri
import android.util.Log

import com.example.monapp.model.Flower
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference

class FirebaseController {

    private val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()

    // Ajouter une fleur à Firestore
    fun addFleur(fleur: Flower, onComplete: (Boolean) -> Unit) {
        val fleurId = firestore.collection("fleurs").document().id // Crée une nouvelle référence de document unique

        val fleurMap = hashMapOf(
            "nom" to fleur.nom,
            "categorie" to fleur.categorie,
            "prix" to fleur.prix,
            "description" to fleur.description,
            "imageUrl" to fleur.imageUrl,
            "userId" to fleur.userId
        )

        firestore.collection("fleurs")
            .document(fleurId)  // Assurez-vous de définir un document unique
            .set(fleurMap)
            .addOnSuccessListener {
                onComplete(true)
            }
            .addOnFailureListener { e ->
                onComplete(false)
            }
    }

    // Uploader une image et obtenir son URL
    fun uploadImage(imageUri: Uri, onComplete: (String?) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = "images/${System.currentTimeMillis()}.jpg"  // Nom unique pour chaque image
        val imageRef = storageRef.child(fileName)

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                imageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        onComplete(uri.toString())  // Renvoie l'URL de l'image
                    }
                    .addOnFailureListener { exception ->
                        onComplete(null)  // En cas d'échec
                    }
            }
            .addOnFailureListener { exception ->
                onComplete(null)  // En cas d'échec
            }
    }
}