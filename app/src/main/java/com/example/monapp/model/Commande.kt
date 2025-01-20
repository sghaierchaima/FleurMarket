package com.example.monapp.model

import com.google.firebase.Timestamp

data class Commande(
    val id: String? = null, // ID de la commande, généré automatiquement par Firestore
    val utilisateurId: String, // ID de l'utilisateur ayant passé la commande
    val date: Timestamp, // Date de la commande
    val prixTotal: Double, // Prix total de la commande
    val produits: List<ProduitCommande>
)
