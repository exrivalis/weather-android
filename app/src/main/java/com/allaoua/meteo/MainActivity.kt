package com.allaoua.meteo

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearSnapHelper
import androidx.recyclerview.widget.SnapHelper
import com.allaoua.meteo.adapter.RecyclerViewAdapter
import com.allaoua.meteo.model.Temperature
import com.allaoua.meteo.model.User
import com.allaoua.meteo.model.Ville
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.firestore.ktx.toObject
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_main.materialToolbar
import kotlinx.android.synthetic.main.activity_main.recyclerView
import kotlinx.android.synthetic.main.activity_main.townName
import kotlinx.android.synthetic.main.activity_town.*
import kotlinx.android.synthetic.main.ville_item.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.net.URL
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.util.*
import kotlin.collections.ArrayList


class MainActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var db : FirebaseFirestore
    private lateinit var adapter:RecyclerViewAdapter
    private var userId:String = ""
    private val TAG:String = "MainActivity"
    // tableau pour enregistrer les jours de la semaine
    private val days = arrayOf("Lundi", "Mardi", "Mercredi", "Jeudi", "Vendredi", "Samedi", "Diamnche")

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        // recuperer la collection de firebase
        db = Firebase.firestore

        // receuperer l'id du user conncecté
        auth = Firebase.auth
        userId= auth.currentUser!!.uid

        // snaphelper on utilise pour faire basculer les ville horizontalement
        val helper: SnapHelper = LinearSnapHelper()
        helper.attachToRecyclerView(recyclerView)

        // gestion ajout nouvelle ville
        addTown.setOnClickListener {
            startActivity(Intent(this,SearchActivity::class.java))
        }

        newTownBtn.setOnClickListener {
            startActivity(Intent(this,SearchActivity::class.java))
        }

        // on attache la toolbar a notre vue
        setSupportActionBar(materialToolbar);
        // cacher le titre dans la toolbar
        getSupportActionBar()?.setDisplayShowTitleEnabled(false);

    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onStart() {
        super.onStart()
        // recuperer la ville preferé de user connecté
        val userDocRef = db.collection("users").document(userId)
        userDocRef.get()
            .addOnSuccessListener { documentSnapshot ->
                if (documentSnapshot != null) {
                    if(documentSnapshot.exists()){
                        // le user existe dans la collection
                        //Log.d(TAG, "DocumentSnapshot data: ${document.data}")

                        // si le champs villes est null ou vide ou ne fait rien
                        val user = documentSnapshot.toObject<User>()
                        if(user?.villes!=null &&  !user?.villes.isEmpty()){
                            // sinon on affiche la ville favorit ou la premiere de la liste villes
                            // user existe donc on cache le addTownlayout qui s affiche quand ce n est pas cas c ad user existe pas et il n a pas de ville
                            addTownLayout.visibility = View.GONE
                            //on recupere la ville
                            var ville: Ville? = user.villes.find { it.isFavorit() == true }
                            if(ville == null)
                                ville = user.villes.get(0)
                            // update nom ville
                            townName.text = ville?.getNom()
                            // récup données depuis weathermap API
                            //val baseUrl = "https://api.openweathermap.org/data/2.5/weather?appid=f1530cb3697c505e9cb8c3ee3fa2481e&units=metric&q="
                            val baseUrl = "https://api.openweathermap.org/data/2.5/forecast?appid=f1530cb3697c505e9cb8c3ee3fa2481e&units=metric&q="
                            val url = baseUrl + ville.getNom()
                            doAsync {
                                val respJsonStr = URL(url).readText()
                                val json = JSONObject(respJsonStr)

                                // remplir le recyclerview qui a besoin d une arraylist de temperature
                                var temperatures:ArrayList<Temperature> = ArrayList<Temperature>()

                                // recuperer la liste des temerature de la ville pour 5 jours et tous les 3 heure on a 40 element de la liste
                                val listTemp = json.getJSONArray("list")
                                // parcourir avec un pas de 8 dans la boucle pour avoir la temperature de la ville pour les ( jours  pendant ces " heure là
                                for(i in 0..39 step 8){
                                    // recuperer un element de la liste
                                    val elem = listTemp.getJSONObject(i)
                                    // recuperer la champs main qui contient les temperature
                                    val main = elem.getJSONObject("main")
                                    val temp = main.getDouble("temp").toInt()

                                    // récupérer le jour de cette temperature
                                    val dayIndex = LocalDate.parse(elem.getString("dt_txt"), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")).dayOfWeek.value
                                    val todayIndex = LocalDate.now().dayOfWeek.value

                                    val day:String
                                    if(dayIndex == todayIndex)
                                        day = "Aujourd'hui"
                                    else
                                        day = days[dayIndex-1]

                                    // ajouter la jour et la temperature 0 la liste des temperature que le recyclevieu utilise
                                    temperatures.add(Temperature(day, temp))
                                }

                                // lancer deasync ne revoit vers une autre thread  runOnUiThread ne permet d executer un code sur la thread principale seule autoriser a modifier la vue
                                runOnUiThread {
                                    // Stuff that updates the UI
                                    recyclerView.apply {
                                        layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
                                        adapter = RecyclerViewAdapter(temperatures)
                                    }
                                }
                            }
                        }
                    }else{
                        Log.d(TAG, "No such document")

                    }
                } else {
                    Log.d(TAG, "No such document")
                }
            }
            .addOnFailureListener { exception ->
                Log.d(TAG, "get failed with ", exception)
            }

    }

    // ajouter le menu à la tolbar
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        val inflater = menuInflater
        inflater.inflate(R.menu.setting_main_menu, menu)
        return true
    }

    // gerer les button de menu
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.displaytown-> {
                startActivity(Intent(this, ListVillesActivity::class.java))
                true
            }
            R.id.signout-> {
                auth.signOut()
                startActivity(Intent(this, LoginActivity::class.java))
                true
            }

            else -> super.onOptionsItemSelected(item)
        }
    }

}