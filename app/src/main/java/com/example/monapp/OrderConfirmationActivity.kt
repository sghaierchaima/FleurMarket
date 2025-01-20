package com.example.monapp

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.firestore.FirebaseFirestore


class OrderConfirmationActivity : AppCompatActivity() {
    private lateinit var detailsTextView: TextView
    private lateinit var btnConfirmer: Button
    private lateinit var btnAnnuler: Button
    private lateinit var paymentMethodSpinner: Spinner
    private lateinit var deliveryMethodSpinner: Spinner

    private var commande: HashMap<String, Any>? = null
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_confirmation)

        // Initialiser les composants UI
        detailsTextView = findViewById(R.id.detailsTextView)
        btnConfirmer = findViewById(R.id.btnConfirmer)
        btnAnnuler = findViewById(R.id.btnAnnuler)
        paymentMethodSpinner = findViewById(R.id.paymentMethodSpinner)
        deliveryMethodSpinner = findViewById(R.id.deliveryMethodSpinner)

        // Charger les options pour les spinners
        setupSpinners()

        // Désactiver le bouton confirmer par défaut
        btnConfirmer.isEnabled = false

        // Gérer les changements de sélection dans les spinners
        val onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                checkSelections()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
        paymentMethodSpinner.onItemSelectedListener = onItemSelectedListener
        deliveryMethodSpinner.onItemSelectedListener = onItemSelectedListener

        // Gérer le clic sur le bouton "Confirmer"
        btnConfirmer.setOnClickListener {
            confirmerCommande()
        }

        // Gérer le clic sur le bouton "Annuler"
        btnAnnuler.setOnClickListener {
            annulerCommande()
        }

        // Récupérer les détails de la commande depuis l'intent
        commande = intent.getSerializableExtra("commande") as HashMap<String, Any>?
        afficherDetailsCommande()
    }

    // Charger les options des spinners
    private fun setupSpinners() {
        val paymentMethods = listOf("Sélectionnez une option", "Carte bancaire", "Espèces", "Paypal")
        val deliveryMethods = listOf("Sélectionnez une option", "Livraison à domicile", "Retrait en magasin")

        paymentMethodSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, paymentMethods)
        deliveryMethodSpinner.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, deliveryMethods)
    }

    // Vérifier si les deux spinners ont une sélection valide
    private fun checkSelections() {
        val paymentSelected = paymentMethodSpinner.selectedItemPosition != 0
        val deliverySelected = deliveryMethodSpinner.selectedItemPosition != 0
        btnConfirmer.isEnabled = paymentSelected && deliverySelected
    }

    // Afficher les détails de la commande
    private fun afficherDetailsCommande() {
        commande?.let {
            val produits = it["produits"] as List<HashMap<String, Any>>
            val produitsDetails = produits.joinToString("\n") { produit ->
                "Produit: ${produit["nom"]}, Quantité: ${produit["quantite"]}, Prix: ${produit["prix"]} TND"
            }
            val total = it["prixTotal"]
            detailsTextView.text = "Détails de la commande :\n$produitsDetails\n\nTotal: $total TND"
        }
    }

    // Confirmer la commande
    private fun confirmerCommande() {
        val paymentMethod = paymentMethodSpinner.selectedItem.toString()
        val deliveryMethod = deliveryMethodSpinner.selectedItem.toString()

        commande?.let {
            it["methodePaiement"] = paymentMethod
            it["methodeLivraison"] = deliveryMethod

            firestore.collection("commandes")
                .add(it)
                .addOnSuccessListener {
                    Toast.makeText(this, "Commande confirmée avec succès !", Toast.LENGTH_LONG).show()
                    viderPanier()
                    startActivity(Intent(this, MainActivity::class.java))
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Erreur : ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }

    // Annuler la commande
    private fun annulerCommande() {
        Toast.makeText(this, "Commande annulée.", Toast.LENGTH_SHORT).show()
        finish()
    }

    // Vider le panier
    private fun viderPanier() {
        CartManager.clearCart()
    }
}