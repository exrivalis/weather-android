package com.allaoua.meteo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.allaoua.meteo.adapter.RecyclerViewAdapter
import com.allaoua.meteo.model.Temperature
import com.allaoua.meteo.model.Ville
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.recyclerView
import kotlinx.android.synthetic.main.activity_main.townName
import kotlinx.android.synthetic.main.activity_search.*
import kotlinx.android.synthetic.main.activity_search.backBtn
import kotlinx.android.synthetic.main.activity_town.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class TownActivity : AppCompatActivity() {

    private lateinit var ville:Ville
    private lateinit var auth: FirebaseAuth
    private lateinit var userId:String
    private  lateinit var db:FirebaseFirestore

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_town)

        // firebase
        // recuperer la collection de firebase
        db = Firebase.firestore

        // receuperer l'id du user conncecté
        auth = Firebase.auth
        userId= auth.currentUser!!.uid


        // on attache la toolbar a notre vue
        setSupportActionBar(materialToolbar);
        // cacher le titre dans la toolbar
        getSupportActionBar()?.setDisplayShowTitleEnabled(false);


        val resp:String? = intent.getStringExtra("resp")
        val villeStr = intent.getStringExtra("ville").toString().toLowerCase().capitalize()
        ville = Ville(villeStr)

        val days = arrayOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Diamnche")

        townName.text = ville.getNom()

        val json = JSONObject(resp)

        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(recyclerView)

        //Log.d(TAG, json.getJSONObject("main").getDouble("temp").toString())

        // remplir le recyclerview
        var temperatures:ArrayList<Temperature> = ArrayList<Temperature>()

        // recuperer la liste des temerature de la ville pour 5 jours et tous les 3 heure on a 40 element de la liste
        val listTemp = json.getJSONArray("list")
        // parcourir avec un pas de 8 dans la boucle pour avoir la temperature de la ville pour les ( jours  pendant ces " heure là
        for(i in 0..39 step 8) {
            // recuperer un element de la liste
            val elem = listTemp.getJSONObject(i)
            // recuperer la champs main qui contient les temperature
            val main = elem.getJSONObject("main")
            val temp = main.getDouble("temp").toInt()

            // récupérer le jour de cette temperature
            val dayIndex = LocalDate.parse(
                elem.getString("dt_txt"),
                DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
            ).dayOfWeek.value - 1
            val todayIndex = LocalDate.now().dayOfWeek.value-1

            val day: String
            if (dayIndex == todayIndex)
                day = "Aujourd'hui"
            else
                day = days[dayIndex]

            // ajouter la jour et la temperature 0 la liste des temperature que le recyclevieu utilise
            temperatures.add(Temperature(day, temp))
        }

            // Stuff that updates the UI
            recyclerView.apply {
                layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                adapter = RecyclerViewAdapter(temperatures)

            }


        // gestion de retour a la page precedente
        backBtn.setOnClickListener {
            onBackPressed()
        }
    }


    // ajouter le menu à la tolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.town_activity_menu, menu)
        return true
    }

    // gerer les button de menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.addTown -> {
                addTown()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }


    // ajouter la ville a la collection firebase
    fun addTown(){
        db.collection("users").document(userId).get().addOnSuccessListener {
            val villes =  it.get("villes") as ArrayList<Ville>

            if(villes.contains(ville)){
                Toast.makeText(this," Ville déjà ajoutée",Toast.LENGTH_LONG).show()
            }
            else{
                villes.add(ville)
                // Update one field, creating the document if it does not already exist.
                val data = hashMapOf("villes" to villes)

                db.collection("users").document(userId)
                    .set(data, SetOptions.merge()).addOnSuccessListener {
                        Toast.makeText(this," Ville ajoutée",Toast.LENGTH_LONG).show()
                    }.addOnFailureListener{
                        Toast.makeText(this," Echec de la sauvegarde ",Toast.LENGTH_LONG).show()
                    }



            }
        }.addOnFailureListener{
            Toast.makeText(this," Echec de la sauvegarde ",Toast.LENGTH_LONG).show()
        }

    }
}