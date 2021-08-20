package com.allaoua.meteo.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.allaoua.meteo.R
import com.allaoua.meteo.model.Temperature

class RecyclerViewAdapter(private val temperatures: ArrayList<Temperature>) :
    RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder>() {




    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val temp: TextView = view.findViewById(R.id.town_temp)
        val day: TextView = view.findViewById(R.id.day_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.meteo_item, parent, false)

        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return temperatures.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.day.text = temperatures.get(position).getDay()
        holder.temp.text = temperatures.get(position).getTemp().toString() + "°"
    }


}
