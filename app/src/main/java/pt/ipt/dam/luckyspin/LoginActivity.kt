package pt.ipt.dam.luckyspin

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam.luckyspin.data.Api
import pt.ipt.dam.luckyspin.data.ApiSheety
import pt.ipt.dam.luckyspin.data.Repository
import pt.ipt.dam.luckyspin.fragmentos.tituloApp
import pt.ipt.dam.luckyspin.fragmentos.tituloAppWelcome
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import java.security.MessageDigest

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

        val usernameInput : EditText = findViewById(R.id.usernameInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val loginButton: Button = findViewById(R.id.loginButton)
        val signUpTextView: TextView = findViewById(R.id.newUserSignUp)

        val rep = Repository()

        loginButton.setOnClickListener {
            val username = usernameInput.text.toString().trim().lowercase()
            val password = passwordInput.text.toString().trim()

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            rep.getUsers { users ->
                if (users != null) {
                    val user = users.find { it.username == username }
                    if (user != null && user.hashPass == hashPass(password)) {
                        Toast.makeText(this, "Login bem-sucedido!", Toast.LENGTH_SHORT).show()

                        writeToFile("user.txt", user.username ?: "")
                        writeToFile("creditos.txt", user.creditos.toString())

                        val intent = Intent(this, RoletaActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this, "Username ou senha incorretos", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(this, "Erro ao buscar usuários", Toast.LENGTH_SHORT).show()
                }
            }



        }
        signUpTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
        }

    }


    private fun hashPass(input: String, algorithm: String = "SHA-256"): String {
        val bytes = MessageDigest.getInstance(algorithm).digest(input.toByteArray())
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }


    private fun writeToFile(fileName: String, dataToSave: String) {
        try {
            val fileOutputStream : FileOutputStream
            try {
                fileOutputStream = openFileOutput(fileName, MODE_PRIVATE)
                fileOutputStream.write(dataToSave.toByteArray())
                fileOutputStream.close()
            } catch (e: Exception) {
                e.printStackTrace()
            }

        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

}
