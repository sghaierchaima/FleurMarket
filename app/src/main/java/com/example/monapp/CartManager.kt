package com.example.monapp

import com.example.monapp.model.CartItem
import com.example.monapp.model.Flower

object CartManager {
    private val cart: MutableList<CartItem> = mutableListOf()

    // Interface pour notifier les mises à jour du panier
    private var cartUpdateListener: CartUpdateListener? = null

    // Définir l'écouteur pour les mises à jour
    fun setCartUpdateListener(listener: CartUpdateListener) {
        cartUpdateListener = listener
    }

    // Interface pour écouter les changements dans le panier
    interface CartUpdateListener {
        fun onCartUpdated()
    }

    fun addFlowerToCart(flower: Flower) {
        // Vérifier si la fleur est déjà dans le panier
        val existingItem = cart.find { it.flower == flower }
        if (existingItem != null) {
            // Incrémenter la quantité si déjà présent
            existingItem.quantity++
        } else {
            // Ajouter comme nouvel élément avec une quantité de 1
            cart.add(CartItem(flower, 1))
        }
        // Notifier après ajout ou modification
        cartUpdateListener?.onCartUpdated()
    }

    fun removeFlowerFromCart(flower: Flower) {
        val existingItem = cart.find { it.flower == flower }
        if (existingItem != null) {
            if (existingItem.quantity > 1) {
                existingItem.quantity--
            } else {
                cart.remove(existingItem)
            }
        }
        // Notifier après suppression ou modification
        cartUpdateListener?.onCartUpdated()
    }

    fun getAllCartItems(): List<CartItem> = cart

    fun clearCart() {
        cart.clear()
        // Notifier après vidage du panier
        cartUpdateListener?.onCartUpdated()
    }
}
