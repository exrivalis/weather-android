package com.allaoua.meteo.model

class Ville constructor(private val nom: String, private var favorit: Boolean = false){


    fun getNom():String{
        return nom
    }

    fun isFavorit(): Boolean{
        return favorit
    }

    fun setFavorit(isFavorit: Boolean){
        favorit = isFavorit
    }
    constructor() : this("", false)
}