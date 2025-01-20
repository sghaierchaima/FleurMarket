package com.example.monapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monapp.databinding.ActivityFlowerBBinding
import com.example.monapp.model.Flower
import com.google.firebase.firestore.FirebaseFirestore

class FlowerActivityB : AppCompatActivity() {

    private lateinit var binding: ActivityFlowerBBinding
    private val firestore = FirebaseFirestore.getInstance()
    private lateinit var flowersAdapter: FlowersAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityFlowerBBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Récupérer le nom de la boutique depuis l'intent
        val shopName = intent.getStringExtra("shopName")

        // Vérifier si shopName est nul
        if (shopName == null) {
            Toast.makeText(this, "Nom de la boutique introuvable", Toast.LENGTH_SHORT).show()
            return
        }

        // Afficher le nom de la boutique



        // Initialiser l'adaptateur et le RecyclerView
        flowersAdapter = FlowersAdapter()
        binding.recyclerViewFlowers.layoutManager = LinearLayoutManager(this)
        binding.recyclerViewFlowers.adapter = flowersAdapter

        // Charger les fleurs de la boutique
        loadFlowersByShopName(shopName)

        // Ajouter un clic sur le nom de la boutique pour afficher une nouvelle page
        binding.shopNameTextView.setOnClickListener {
            val intent = Intent(this, FlowersAdapter::class.java).apply {
                putExtra("shopName", shopName)
            }
            startActivity(intent)
        }
    }

    private fun loadFlowersByShopName(shopName: String) {
        // Étape 1: Rechercher l'userId du shopName dans la collection users
        firestore.collection("users")
            .whereEqualTo("shopName", shopName)  // Chercher le userId correspondant à shopName
            .get()
            .addOnSuccessListener { userDocuments ->
                if (userDocuments.isEmpty) {
                    Toast.makeText(this, "Aucun utilisateur trouvé pour ce shopName", Toast.LENGTH_SHORT).show()
                    return@addOnSuccessListener
                }

                // Récupérer l'userId (en supposant qu'il existe un seul utilisateur pour ce shopName)
                val userId = userDocuments.documents[0].id

                // Étape 2: Utiliser userId pour récupérer les fleurs de ce utilisateur dans la collection fleurs
                loadFlowersByUserId(userId)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erreur lors de la recherche de l'utilisateur : ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun loadFlowersByUserId(userId: String) {
        // Étape 3: Charger les fleurs associées à l'userId
        firestore.collection("fleurs")
            .whereEqualTo("userId", userId)  // Filtrer les fleurs par userId
            .get()
            .addOnSuccessListener { documents ->
                val flowersList = ArrayList<Flower>()
                for (document in documents) {
                    val flower = document.toObject(Flower::class.java)
                    flowersList.add(flower)
                }
                flowersAdapter.setData(flowersList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erreur : ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}