package com.allaoua.meteo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.allaoua.meteo.R
import com.allaoua.meteo.model.User
import com.allaoua.meteo.model.Ville
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.toObject


class ListeVilleAdapter(private val context: Context, private var villes: ArrayList<Ville>, var clickListener: TownClickListener) :
    RecyclerView.Adapter<ListeVilleAdapter.ViewHolder>() {



    private   var  db:FirebaseFirestore = FirebaseFirestore.getInstance()
    private  var auth: FirebaseAuth = FirebaseAuth.getInstance()
    private var  userId:String = auth.currentUser!!.uid


    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val nomVille: TextView = view.findViewById(R.id.nomVille)
        val favorit: ImageView = view.findViewById(R.id.favorit)

        fun start(ville: Ville, clickListener: TownClickListener){
            itemView.setOnClickListener{
                clickListener.onClick(ville, adapterPosition)

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.ville_item, parent, false)

        return ViewHolder(view)

    }

    override fun getItemCount(): Int {
        return villes.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.nomVille.text = villes.get(position).getNom()

        holder.start(villes.get(position), clickListener)

        if(villes.get(position).isFavorit()){
            holder.favorit.setImageResource(R.drawable.ic_star_24)
        }else
            holder.favorit.setImageResource(R.drawable.ic_star_empty_24)

        holder.favorit.setOnClickListener{
            var newFavorit = false

             if(!villes.get(position).isFavorit()){
                newFavorit = true
             }

            db.collection("users").document(userId).get().addOnSuccessListener {


                villes.forEach {
                  it.setFavorit(false)
                }
                villes[position].setFavorit(newFavorit)

                val data = hashMapOf("villes" to villes)

                db.collection("users").document(userId)
                    .set(data, SetOptions.merge()).addOnSuccessListener {
                        if(newFavorit)
                            holder.favorit.setImageResource(R.drawable.ic_star_24)
                        else
                            holder.favorit.setImageResource(R.drawable.ic_star_empty_24)

                        notifyDataSetChanged()
                        Toast.makeText(context," Valeur mise à jour ", Toast.LENGTH_SHORT).show()

                    }.addOnFailureListener{
                        Toast.makeText(context," Echec de la mise à jour ", Toast.LENGTH_SHORT).show()
                    }
            }
                .addOnFailureListener {
                    Toast.makeText(context," Echec de la lecture de la donnée ", Toast.LENGTH_SHORT).show()
                }
        }
    }

}

interface TownClickListener {
    fun onClick(item: Ville, position: Int)
    fun onLongClick(view: View?, position: Int)
}

