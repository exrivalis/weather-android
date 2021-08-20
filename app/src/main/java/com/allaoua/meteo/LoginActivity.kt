package com.allaoua.meteo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)

        // Initialize Firebase Auth
        auth = Firebase.auth

        loginBtn.setOnClickListener {
            val login = loginInput.text.toString()
            val password = passInput.text.toString() ;
            // si les champs ne sont pas vide on peut verifier l'autenticité
            if(login.isEmpty() || password.isEmpty()){
                    // si les champs de login et password ne sont pas rempli on affiche un message pour dire veuillez remplir tous les champs
                Toast.makeText(this, " veuillez remplir tous les champs", Toast.LENGTH_LONG).show()
            }else{

                //authentification avec firebase  avec champs emil et champs de password tapé par l'utilasateur
                auth.signInWithEmailAndPassword(login, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {

                            // SI L UTILISATEUR ARRIVE A SE CONNECTER ON AFFICHE ACTIVITY PRINCIPALE MAINACTIVITY
                            startActivity(Intent(this, MainActivity::class.java))
                            // finish() por ecraser activity actuelle et demarrer une autre pour eviter que les activity se superposent
                            finish()

                        } else {
                            // If sign in fails, display a message to the user
                            Toast.makeText(baseContext, "Authentication failed.",
                                Toast.LENGTH_SHORT).show()
                            // ...
                        }

                        // ...
                    }


            }
        }

        // aller activity cree new compte
        createBtn.setOnClickListener {
            startActivity(Intent(this, CreateUserActivity::class.java))
            finish()
        }
    }

    // lancer l activity
    public override fun onStart() {
        super.onStart()
        // Check if user is signed in (non-null) and update UI accordingly.
        val currentUser = auth.currentUser
        if(currentUser != null){
            startActivity(Intent(this, MainActivity::class.java))
            finish()
        }
    }
}