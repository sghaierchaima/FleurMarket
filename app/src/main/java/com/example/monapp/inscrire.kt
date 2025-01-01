package com.example.monapp

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.monapp.databinding.ActivityInscrireBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class inscrire : AppCompatActivity() {

    private lateinit var binding: ActivityInscrireBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInscrireBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Initialisation de Firebase Auth et Firestore
        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        // Gérer la visibilité du champ Nom de la boutique
        binding.radioGroup.setOnCheckedChangeListener { _, checkedId ->
            if (checkedId == R.id.radioAdmin) {
                binding.shopName.visibility = View.VISIBLE
            } else {
                binding.shopName.visibility = View.GONE
                binding.shopName.text?.clear() // Effacer le champ si non admin
            }
        }

        binding.continueBtn.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()
            val shopName = binding.shopName.text.toString().trim()

            // Validation des champs
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email et mot de passe ne peuvent pas être vides.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            if (password.length < 6) {
                Toast.makeText(this, "Le mot de passe doit contenir au moins 6 caractères.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Récupération du rôle sélectionné
            val selectedRole = when (binding.radioGroup.checkedRadioButtonId) {
                R.id.radioAdmin -> "admin"
                R.id.radioUser -> "user"
                else -> "user" // Rôle par défaut
            }

            // Validation du champ Nom de la boutique si admin
            if (selectedRole == "admin" && shopName.isEmpty()) {
                Toast.makeText(this, "Le nom de la boutique est obligatoire pour les administrateurs.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Création de l'utilisateur dans Firebase Auth
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        val user = auth.currentUser
                        val userMap = hashMapOf(
                            "email" to email,
                            "role" to selectedRole,
                            "shopName" to if (selectedRole == "admin") shopName else null
                        )

                        // Ajouter les données de l'utilisateur dans Firestore
                        firestore.collection("users").document(user!!.uid)
                            .set(userMap)
                            .addOnSuccessListener {
                                Log.d(TAG, "Rôle utilisateur ajouté : $selectedRole")
                                if (selectedRole == "admin") {
                                    val intent = Intent(this, HomeActivity::class.java)
                                    startActivity(intent)
                                } else {
                                    val intent = Intent(this, MainActivity::class.java)
                                    startActivity(intent)
                                }
                            }
                            .addOnFailureListener { e ->
                                Log.w(TAG, "Erreur lors de l'ajout du rôle utilisateur", e)
                                Toast.makeText(this, "Impossible d'ajouter le rôle : ${e.message}", Toast.LENGTH_SHORT).show()
                            }
                    } else {
                        val errorMessage = task.exception?.message ?: "Erreur inconnue"
                        Log.w(TAG, "Erreur lors de la création de l'utilisateur", task.exception)
                        Toast.makeText(this, "Échec de l'inscription : $errorMessage", Toast.LENGTH_SHORT).show()
                    }
                }
        }

        binding.move.setOnClickListener {
            val intent = Intent(this, login::class.java)
            startActivity(intent)
        }
    }

    companion object {
        private const val TAG = "InscrireActivity"
    }
}