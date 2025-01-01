package com.example.monapp

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
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

class ajouterFleurs : AppCompatActivity() {
    private lateinit var binding: ActivityAjouterFleursBinding
    private val firebaseController = FirebaseController()
    private var imageUri: Uri? = null
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAjouterFleursBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        val currentUser = Firebase.auth.currentUser
        if (currentUser == null) {
            Toast.makeText(this, "Veuillez vous connecter pour ajouter une fleur", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, login::class.java)  // Diriger vers la page de connexion
            startActivity(intent)
            finish()
            return
        }

        val userId = currentUser.uid

        binding.uploadImageButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            resultLauncher.launch(intent)
        }

        binding.addFlowerButton.setOnClickListener {
            val nom = binding.flowerNameInput.text.toString()
            val categorie = binding.flowerCategoryInput.text.toString()
            val prix = binding.flowerPriceInput.text.toString().toDoubleOrNull() ?: 0.0
            val description = binding.flowerDescriptionInput.text.toString()

            if (nom.isEmpty() || categorie.isEmpty() || prix <= 0.0 || description.isEmpty()) {
                Toast.makeText(this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val fleur = Flower(nom, categorie, prix, description, "", userId)

            if (imageUri != null) {
                firebaseController.uploadImage(imageUri!!) { imageUrl ->
                    fleur.imageUrl = imageUrl ?: ""
                    addFleurToDatabase(fleur)
                }
            } else {
                addFleurToDatabase(fleur)
            }
        }
    }

    private val resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == RESULT_OK) {
                val data: Intent? = result.data
                imageUri = data?.data
                // Affichage de l'image sélectionnée avec Coil
                binding.imageView.load(imageUri) {
                    crossfade(true)
                    transformations(RoundedCornersTransformation(16f))  // Optionnel : ajout d'une bordure arrondie
                }
            }
        }

    private fun addFleurToDatabase(fleur: Flower) {
        val fleurMap = hashMapOf(
            "nom" to fleur.nom,
            "categorie" to fleur.categorie,
            "prix" to fleur.prix,
            "description" to fleur.description,
            "imageUrl" to fleur.imageUrl,
            "userId" to fleur.userId
        )

        firestore.collection("fleurs")
            .add(fleurMap)
            .addOnSuccessListener {
                Toast.makeText(this, "Fleur ajoutée avec succès", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur lors de l'ajout de la fleur", Toast.LENGTH_SHORT).show()
                e.printStackTrace()  // Pour afficher l'erreur dans les logs
            }
    }
}