package com.example.monapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import coil.load
import coil.transform.RoundedCornersTransformation
import com.example.monapp.controllers.FirebaseController
import com.example.monapp.databinding.ActivityAjouterFleursBinding
import com.example.monapp.model.Flower
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.util.UUID

class ajouterFleurs : AppCompatActivity() {
    private lateinit var binding: ActivityAjouterFleursBinding
    private var imageUri: Uri? = null
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAjouterFleursBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Sélectionner une image
        binding.uploadImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }

        // Ajouter la fleur
        binding.addFlowerButton.setOnClickListener {
            val nom = binding.flowerNameInput.text.toString()
            val categorie = binding.flowerCategoryInput.text.toString()
            val prix = binding.flowerPriceInput.text.toString().toDoubleOrNull() ?: 0.0
            val description = binding.flowerDescriptionInput.text.toString()

            // Validation des champs
            if (nom.isEmpty() || categorie.isEmpty() || prix <= 0.0 || description.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Créer un objet Flower sans image URL pour l'instant
            val flower = Flower(nom, categorie, prix, description, "", Firebase.auth.currentUser?.uid ?: "")

            // Si une image a été sélectionnée, on l'upload
            if (imageUri != null) {
                uploadImage(imageUri!!) { imageUrl ->
                    flower.imageUrl = imageUrl ?: ""
                    addFleurToDatabase(flower)
                }
            } else {
                // Si pas d'image, on sauvegarde la fleur sans image
                addFleurToDatabase(flower)
            }
        }
    }

    // Lancer l'activité pour choisir une image
    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                // Afficher l'image dans un ImageView avec Coil
                binding.imageView.load(imageUri) {
                    crossfade(true)
                    transformations(RoundedCornersTransformation(16f)) // Coins arrondis
                }
            }
        }

    // Méthode pour uploader l'image dans Firebase Storage
    private fun uploadImage(imageUri: Uri, onComplete: (String?) -> Unit) {
        val storageRef = FirebaseStorage.getInstance().reference
        val fileName = "photos/${UUID.randomUUID()}.jpg" // Utilisation d'un UUID unique pour éviter les collisions
        val imageRef = storageRef.child(fileName)

        // Log pour déboguer
        Log.d("Firebase", "Uploading image to $fileName")

        imageRef.putFile(imageUri)
            .addOnSuccessListener {
                // Lorsque l'upload réussit, on récupère l'URL de l'image
                Log.d("Firebase", "Upload success, getting download URL...")

                imageRef.downloadUrl
                    .addOnSuccessListener { uri ->
                        Log.d("Firebase", "Download URL received: $uri")
                        onComplete(uri.toString())  // Retourner l'URL de l'image
                    }
                    .addOnFailureListener { exception ->
                        Log.e("Firebase", "Failed to get download URL", exception)
                        onComplete(null)  // En cas d'échec
                    }
            }
            .addOnFailureListener { exception ->
                Log.e("Firebase", "Image upload failed", exception)
                onComplete(null)  // En cas d'échec
            }
    }

    // Méthode pour ajouter la fleur dans Firestore
    private fun addFleurToDatabase(flower: Flower) {
        // Créer un HashMap pour Firestore
        val flowerMap = hashMapOf(
            "nom" to flower.nom,
            "categorie" to flower.categorie,
            "prix" to flower.prix,
            "description" to flower.description,
            "imageUrl" to flower.imageUrl,
            "userId" to flower.userId
        )

        firestore.collection("fleurs")
            .add(flowerMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Fleur ajoutée avec succès", Toast.LENGTH_SHORT).show()
                finish() // Fermer l'activité après l'ajout
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur lors de l'ajout de la fleur", Toast.LENGTH_SHORT).show()
                e.printStackTrace()
            }
    }
}