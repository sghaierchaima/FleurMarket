package com.example.monapp

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.monapp.model.Flower

class FlowersAdapter  : RecyclerView.Adapter<FlowersAdapter.FlowerViewHolder>() {

    private var flowersList: List<Flower> = ArrayList()

    fun setData(list: List<Flower>) {
        flowersList = list
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): FlowerViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_flower_b, parent, false)
        return FlowerViewHolder(view)
    }

    override fun onBindViewHolder(holder: FlowerViewHolder, position: Int) {
        holder.bind(flowersList[position])
    }

    override fun getItemCount(): Int {
        return flowersList.size
    }

    inner class FlowerViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val flowerNameTextView: TextView = itemView.findViewById(R.id.flowerNameTextView)
        private val flowerPriceTextView: TextView = itemView.findViewById(R.id.flowerPriceTextView)
        private val flowerdescriptionTextView: TextView = itemView.findViewById(R.id.descriptionTextView)
        private val addToCartIcon: ImageView = itemView.findViewById(R.id.addToCartIcon)

        fun bind(flower: Flower) {
            flowerNameTextView.text = "Catégorie : ${flower.nom}"
            flowerPriceTextView.text = "Prix : ${flower.prix} TND"
            flowerdescriptionTextView.text = "Description : ${flower.description}"
            // Ajouter un clic sur l'élément pour afficher l'icône
            itemView.setOnClickListener {
                addToCartIcon.visibility = View.VISIBLE

                // Cacher l'icône après 2 secondes
                addToCartIcon.setOnClickListener {
                    CartManager.addFlowerToCart(flower) // Ajouter la fleur au panier

                    // Afficher un message pour confirmer l'ajout (Toast)
                    itemView.context.toast("Fleur ajoutée au panier !")
                }

            }
        }
    }
    fun android.content.Context.toast(message: String) {
        android.widget.Toast.makeText(this, message, android.widget.Toast.LENGTH_SHORT).show()
    }
}
