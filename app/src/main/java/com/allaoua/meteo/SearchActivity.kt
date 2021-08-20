package com.allaoua.meteo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.allaoua.meteo.adapter.RecyclerViewAdapter
import com.allaoua.meteo.model.Temperature
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.activity_search.*
import org.jetbrains.anko.doAsync
import org.json.JSONObject
import java.lang.Exception
import java.net.URL
import java.nio.charset.Charset


class SearchActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)

        // aficher le clavier au lancement
        searchInput.requestFocus()

        val inputMethodManager =
            baseContext.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        inputMethodManager.toggleSoftInput(
            InputMethodManager.SHOW_FORCED,
            InputMethodManager.HIDE_IMPLICIT_ONLY
        )


        // on search effectuer la rechrche et afficher le resultat
        searchInput.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){

                // lancer la recherche
                val  villeRecherche : String= searchInput.text.toString()
                if(villeRecherche.isEmpty()){
                    Toast.makeText(this,"champs vide",Toast.LENGTH_LONG).show()
                }else{
                    search(villeRecherche)
                }


                true
            } else {
                false
            }
        }


        // retour
        backBtn.setOnClickListener {
            hideKeyboard()
            finish()
        }
    }


    fun Fragment.hideKeyboard() {
        view?.let { activity?.hideKeyboard(it) }
    }

    fun Activity.hideKeyboard() {
        hideKeyboard(currentFocus ?: View(this))
    }

    fun Context.hideKeyboard(view: View) {
        val inputMethodManager = getSystemService(Activity.INPUT_METHOD_SERVICE) as InputMethodManager
        inputMethodManager.hideSoftInputFromWindow(view.windowToken, 0)
    }

    fun search(ville:String){
        val baseUrl = "https://api.openweathermap.org/data/2.5/forecast?appid=f1530cb3697c505e9cb8c3ee3fa2481e&units=metric&q="
        val url = baseUrl + ville

        doAsync {
            var respJsonStr:String = ""
            var json:JSONObject
            var respCode = "400"

            try {
                respJsonStr = URL(url).readText(Charset.forName("ISO-8859-1"))
                json = JSONObject(respJsonStr)
                respCode = json.getString("cod")
            }catch (e: Exception){

            }


            System.out.println(respJsonStr)

            if(!respCode.equals("200")) {
                runOnUiThread{
                    errorMessage.visibility= View.VISIBLE
                }
            }else{
                runOnUiThread {
                    errorMessage.visibility= View.GONE
                }

                val intent = Intent(baseContext, TownActivity::class.java)
                intent.putExtra("resp", respJsonStr)
                intent.putExtra("ville", ville.capitalize())
                hideKeyboard()
                startActivity(intent)
            }



        }
    }
}