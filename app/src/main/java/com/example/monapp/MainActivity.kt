package com.example.monapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.monapp.databinding.ActivityMainBinding
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var nomBoutique: nomBoutique
    private val firestore = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setupRecyclerView()
        fetchNameshopData()

        binding.logoutBtn.setOnClickListener {
            Firebase.auth.signOut()
            Toast.makeText(this, "Déconnexion réussie", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, login::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun setupRecyclerView() {
        nomBoutique = nomBoutique()
        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(this@MainActivity)
            adapter = nomBoutique
        }
    }

    private fun fetchNameshopData() {
        firestore.collection("users")
            .get()
            .addOnSuccessListener { documents ->
                val nameshopList = ArrayList<String>()
                for (document in documents) {
                    val shopName = document.getString("shopName")
                    if (shopName != null) {
                        nameshopList.add(shopName)
                    }
                }
                nomBoutique.setData(nameshopList)
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Erreur : ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }
}