package com.example.monapp

import android.content.ContentValues.TAG
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.monapp.databinding.ActivityLoginBinding
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class login : AppCompatActivity() {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        binding.continueBtn.setOnClickListener {
            val email = binding.email.text.toString().trim()
            val password = binding.password.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Veuillez entrer un email et un mot de passe", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Connexion réussie
                        val user = auth.currentUser
                        if (user != null) {
                            checkUserRoleAndRedirect(user.uid)
                        }
                    } else {
                        // Échec de connexion
                        Log.w(TAG, "signInWithEmail:failure", task.exception)
                        Toast.makeText(
                            baseContext,
                            "Échec de l'authentification. Veuillez vérifier vos identifiants.",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
        }

        binding.move.setOnClickListener {
            val intent = Intent(this, inscrire::class.java)
            startActivity(intent)
        }
    }

    override fun onStart() {
        super.onStart()
        // Vérifier si l'utilisateur est déjà connecté
        val currentUser = auth.currentUser
        if (currentUser != null) {
            checkUserRoleAndRedirect(currentUser.uid)
        }
    }

    private fun checkUserRoleAndRedirect(userId: String) {
        firestore.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    val role = document.getString("role") ?: "user"
                    if (role == "admin") {
                        // Redirection pour l'admin
                        val intent = Intent(this, HomeActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Redirection pour un utilisateur normal
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    }
                    finish() // Terminer l'activité de connexion
                } else {
                    Log.d(TAG, "Aucun document trouvé pour l'utilisateur")
                }
            }
            .addOnFailureListener { e ->
                Log.w(TAG, "Erreur lors de la récupération du rôle utilisateur", e)
                Toast.makeText(
                    this,
                    "Impossible de vérifier le rôle de l'utilisateur.",
                    Toast.LENGTH_SHORT
                ).show()
            }
    }
}
