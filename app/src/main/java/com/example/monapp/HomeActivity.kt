package com.example.monapp

import android.content.Intent
import android.graphics.drawable.AnimationDrawable
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monapp.databinding.ActivityHomeBinding
import com.example.monapp.model.Flower
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase

class HomeActivity : AppCompatActivity() {
    private lateinit var binding: ActivityHomeBinding
    private lateinit var flowerAdapter: flowers_item
    private val flowers = mutableListOf<Flower>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Liaison avec le fichier XML via View Binding
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val recyclerView = binding.flowersRecyclerView
        recyclerView.layoutManager = LinearLayoutManager(this)
        flowerAdapter = flowers_item(flowers)
        recyclerView.adapter = flowerAdapter

        // Animation pour l'image
        val animatedImage = findViewById<ImageView>(R.id.animated_image)
        animatedImage.setBackgroundResource(R.drawable.flower_animation)
        val animationDrawable = animatedImage.background as AnimationDrawable
        animationDrawable.start()

        // Gestion des boutons
        binding.addFlowerButton.setOnClickListener {
            // Rediriger vers l'activité `ajouterFleurs`
            val intent = Intent(this, ajouterFleurs::class.java)
            startActivity(intent)
        }

        binding.logoutBtn.setOnClickListener {
            Firebase.auth.signOut()
            Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, login::class.java)
            startActivity(intent)
            finish()
        }

        // Charger les fleurs filtrées
        fetchFlowers()
    }

    override fun onResume() {
        super.onResume()
        fetchFlowers() // Recharger les données à chaque retour sur cette activité
    }

    private fun fetchFlowers() {
        val db = Firebase.firestore
        val currentUserId = Firebase.auth.currentUser?.uid

        if (currentUserId == null) {
            Toast.makeText(this, "Utilisateur non connecté", Toast.LENGTH_SHORT).show()
            return
        }

        db.collection("fleurs")
            .whereEqualTo("userId", currentUserId) // Filtrer par userId
            .get()
            .addOnSuccessListener { documents ->
                flowers.clear()
                for (document in documents) {
                    val flower = document.toObject(Flower::class.java)
                    flowers.add(flower)
                }
                flowerAdapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erreur : ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }
}
