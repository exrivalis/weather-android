package com.allaoua.meteo

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.allaoua.meteo.adapter.ListeVilleAdapter
import com.allaoua.meteo.adapter.RecyclerViewAdapter
import com.allaoua.meteo.adapter.TownClickListener
import com.allaoua.meteo.model.User
import com.allaoua.meteo.model.Ville
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_list_villes.*
import kotlinx.android.synthetic.main.activity_main.recyclerView
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.net.URL

class ListVillesActivity : AppCompatActivity(), TownClickListener {

    private lateinit var auth: FirebaseAuth
    private lateinit var userId:String
    private  lateinit var db: FirebaseFirestore


    private lateinit var villes:ArrayList<Ville>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list_villes)

        // firebase
        // recuperer la collection de firebase
        db = Firebase.firestore

        // receuperer l'id du user conncecté
        auth = Firebase.auth
        userId= auth.currentUser!!.uid
        villes=ArrayList()


        backBtn.setOnClickListener {
            onBackPressed()
        }




    }

    override fun onStart() {
        super.onStart()
        db.collection("users").document(userId).get().addOnSuccessListener {
            val user = it.toObject(User::class.java)
            if(user?.villes != null){
                villes =  user?.villes as ArrayList<Ville>

                // on attache le recyclevieux

                recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
                recyclerView.adapter = ListeVilleAdapter(baseContext, villes, this)


            }



        }.addOnFailureListener{
            Toast.makeText(this," Echec de la lecture des données ", Toast.LENGTH_LONG).show()
        }
    }


    override fun onClick(item: Ville, position: Int) {
        //var intent = Intent(this, TownActivity::class.java)
        show(item.getNom())
    }

    override fun onLongClick(view: View?, position: Int) {
        TODO("Not yet implemented")
    }

    fun show(ville:String){
        val baseUrl = "https://api.openweathermap.org/data/2.5/forecast?appid=f1530cb3697c505e9cb8c3ee3fa2481e&units=metric&q="
        val url = baseUrl + ville

        doAsync {
            val respJsonStr = URL(url).readText()
            val json = JSONObject(respJsonStr)

            val respCode = json.getInt("cod")
            if(respCode!= 200) {
                Toast.makeText(baseContext," Echec lors de la récupération des données ",Toast.LENGTH_LONG).show()
            }else{

                val intent = Intent(baseContext, TownActivity::class.java)
                intent.putExtra("resp", respJsonStr)
                intent.putExtra("ville", ville.capitalize())
                startActivity(intent)
            }



        }
    }
}