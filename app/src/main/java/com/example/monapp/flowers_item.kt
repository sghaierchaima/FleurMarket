package com.example.monapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.monapp.databinding.ActivityFlowersItemBinding
import com.example.monapp.model.Flower

class flowers_item (private val flowers: List<Flower>) : RecyclerView.Adapter<flowers_item.FlowerViewHolder>() {

    // ViewHolder utilisant le ViewBinding
    class FlowerViewHolder(val binding: ActivityFlowersItemBinding) : RecyclerView.ViewHolder(binding.root) {
        // Liaison des vues avec le ViewBinding
        fun bind(flower: Flower) {
            binding.flowerName.text = flower.nom
            binding.flowerCategory.text = "Catégorie : ${flower.categorie}"
            binding.flowerDescription.text = "Description : ${flower.description}"
            binding.flowerPrice.text = "Prix : ${flower.prix} TND"

            val imageUrl = flower.imageUrl
            if (imageUrl.isNullOrEmpty()) {
                // Charger une image par défaut si l'URL est vide ou nulle
                Glide.with(binding.root.context)
                    .load(R.drawable.warda) // Remplacez par votre image par défaut
                    .into(binding.warda)
            } else {
                // Charger l'image depuis l'URL
                Glide.with(binding.root.context)
                    .load(imageUrl)
                    .into(binding.warda)
            }
            // Clic sur l'icône pour rediriger vers l'activité de modification
            binding.iconeEdit.setOnClickListener {
                val intent = Intent(binding.root.context, editFlowers::class.java).apply {
                    // Passer les informations nécessaires pour la modification
                    putExtra("nom", flower.nom)
                    putExtra("categorie", flower.categorie)
                    putExtra("description", flower.description)
                    putExtra("prix", flower.prix)
                    putExtra("imageUrl", flower.imageUrl)
                }
                binding.root.context.startActivity(intent)
            }
        }
    }

    // Créer et lier le ViewBinding
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowerViewHolder {
        val binding = ActivityFlowersItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FlowerViewHolder(binding)
    }

    // Associer les données aux vues
    override fun onBindViewHolder(holder: FlowerViewHolder, position: Int) {
        val flower = flowers[position]
        holder.bind(flower)
    }

    // Retourner le nombre d'éléments
    override fun getItemCount() = flowers.size
}
