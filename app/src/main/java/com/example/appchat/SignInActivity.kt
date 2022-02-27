package com.example.appchat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.example.appchat.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    lateinit var launcher: ActivityResultLauncher<Intent>
    lateinit var auth : FirebaseAuth
    lateinit var authBinding : ActivitySignInBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        authBinding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(authBinding.root)

        auth = Firebase.auth
        auth.currentUser
        launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
            val task = GoogleSignIn.getSignedInAccountFromIntent(it.data)
            try{
                val account = task.getResult(ApiException::class.java)
                if(account !== null){
                    fbAuthWithGoogle(account.idToken!!)
                }
            }catch (e: ApiException){
                Log.d("MyLog","Api exception")
            }
        }

        authBinding.btSignIn.setOnClickListener {
            signInWithGoogle()
        }
        checkAuthState()
    }

    private fun getClient() : GoogleSignInClient{
        val gso = GoogleSignInOptions
            .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(this, gso)
    }

    private fun signInWithGoogle(){
        val signInClient = getClient()
        launcher.launch(signInClient.signInIntent)
    }

    private fun fbAuthWithGoogle(idToken : String){
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        auth.signInWithCredential(credential).addOnCompleteListener {
            if(it.isSuccessful){
                Log.d("MyLog","Google signIn done")
                checkAuthState()
            } else {
                Log.d("MyLog","Google signIn not done")
            }
        }
    }

    private fun checkAuthState(){
        if(auth.currentUser !== null){
            val i = Intent(this, MainActivity::class.java)
            startActivity(i)
        }
    }

}