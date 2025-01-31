package pt.ipt.dam.luckyspin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam.luckyspin.fragmentos.tituloApp
import pt.ipt.dam.luckyspin.fragmentos.tituloAppWelcome

class LoginActivity : AppCompatActivity() {

    lateinit var f1: tituloApp

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.login)
        f1 = tituloApp.newInstance("", "");
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentAppTitle, f1)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        // Find the Login button
        val loginButton: Button = findViewById(R.id.loginButton)
        // Find the New user? Sign up text view
        val signUpTextView: TextView = findViewById(R.id.newUserSignUp)
        // Set click listener
        loginButton.setOnClickListener {
            // Navigate to RoletaActivity
            val intent = Intent(this, RoletaActivity::class.java)
            startActivity(intent)
        }
        // Set click listener for the New user? Sign up text
        signUpTextView.setOnClickListener {
            // Navigate to RegisterActivity
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }
}
