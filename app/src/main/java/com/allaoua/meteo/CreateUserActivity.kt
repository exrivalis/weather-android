package com.allaoua.meteo


import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import kotlinx.android.synthetic.main.activity_create_user.*
import kotlin.math.log


class CreateUserActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_user)


// Initialize Firebase Auth
        auth = Firebase.auth

        createBtn.setOnClickListener{
            val email = loginInput.text.toString()
            val password=  passInput.text.toString()
            val confirmePassWord = confirmPassInput.text.toString()

            // enlever les messages d'erreurs s'il y en a
            passLayout.setError(null);
            loginLayout.setError(null);
            // les champs ne sont pas tous remplis
            if(email.isEmpty() || password.isEmpty()|| confirmePassWord.isEmpty()){
                Toast.makeText(this, " veuillez remplir tous les champs", Toast.LENGTH_LONG).show()

                // les deux mots de passes sont pas les meme
            } else if(!password.equals(confirmePassWord)){
                Toast.makeText(this, " Mots de passe différents", Toast.LENGTH_LONG).show()

                // champs tous bien rempli on cree un new user
            }else{

                auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this) { task ->
                        if (task.isSuccessful) {
                            startActivity(Intent(this, MainActivity::class.java))
                            finish()


                        } else {

                            if (!task.isSuccessful) {
                                try {
                                    throw task.exception!!
                                } catch (e: FirebaseAuthWeakPasswordException) {
                                    passLayout.setError("Mot de passe faible")
                                    passInput.requestFocus()
                                } catch (e: FirebaseAuthInvalidCredentialsException) {
                                    loginLayout.setError("Email invalide")
                                    loginInput.requestFocus()
                                } catch (e: FirebaseAuthUserCollisionException) {
                                    loginLayout.setError("Email déjà utilisé")
                                    loginInput.requestFocus()
                                } catch (e: Exception) {
                                    Toast.makeText(
                                        baseContext, "Erreur d'authetification",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }

                        }


                    }
            }
        }


        // aller a l'activity de connection
        loginBtn.setOnClickListener {
            startActivity(Intent(baseContext, LoginActivity::class.java))
            finish()
        }

    }

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