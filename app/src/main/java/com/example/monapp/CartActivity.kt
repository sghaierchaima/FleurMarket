package com.example.monapp

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.monapp.model.CartItem
import com.example.monapp.model.Flower
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class CartActivity : AppCompatActivity() {
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var totalPriceTextView: TextView
    private lateinit var btnPasserCommande: Button
    private val cartItems: MutableList<CartItem> = mutableListOf()

    // Firebase instances
    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        // Initialisation des composants UI
        cartRecyclerView = findViewById(R.id.cartRecyclerView)
        totalPriceTextView = findViewById(R.id.totalPriceTextView)
        btnPasserCommande = findViewById(R.id.btnPasserCommande)

        // Charger les éléments du panier
        cartItems.addAll(CartManager.getAllCartItems())

        // Configurer le RecyclerView
        cartRecyclerView.layoutManager = LinearLayoutManager(this)
        cartRecyclerView.adapter = CartAdapter(cartItems)

        // Afficher le total initial
        updateTotalPrice()

        // Événement de clic pour passer la commande
        btnPasserCommande.setOnClickListener {
            passerCommande()
        }
    }

    // Méthode pour passer une commande
    private fun passerCommande() {
        val user = firebaseAuth.currentUser
        if (user != null) {
            // Récupérer l'ID utilisateur et la date actuelle
            val userId = user.uid
            val currentDate = Timestamp.now()

            // Préparer les données de la commande
            val commande = hashMapOf(
                "utilisateurId" to userId,
                "date" to currentDate,
                "prixTotal" to calculateTotalPrice(),
                "produits" to cartItems.map {
                    hashMapOf(
                        "nom" to it.flower.nom,
                        "quantite" to it.quantity,
                        "prix" to it.flower.prix
                    )
                }
            )

            // Rediriger vers OrderConfirmationActivity avec les détails de la commande
            val intent = Intent(this, OrderConfirmationActivity::class.java)
            intent.putExtra("commande", commande)
            startActivity(intent)
        } else {
            Toast.makeText(this, "Veuillez vous connecter pour passer une commande.", Toast.LENGTH_SHORT).show()
        }
    }


    // Méthode pour calculer le total du panier
    private fun calculateTotalPrice(): Double {
        return cartItems.sumOf { it.flower.prix * it.quantity }
    }

    // Méthode pour mettre à jour l'affichage du total
    private fun updateTotalPrice() {
        val total = calculateTotalPrice()
        totalPriceTextView.text = "Total: ${total} TND"
    }
}
