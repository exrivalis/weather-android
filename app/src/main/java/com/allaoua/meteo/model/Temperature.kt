package com.allaoua.meteo.model

class Temperature constructor(day:String, temp:Int){
    private val day:String = day
    private val temp:Int = temp

    fun getDay():String{
        return day
    }

    fun getTemp():Int{
        return temp
    }
}
