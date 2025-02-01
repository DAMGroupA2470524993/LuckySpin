package pt.ipt.dam.luckyspin

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity

import pt.ipt.dam.luckyspin.data.Repository
import pt.ipt.dam.luckyspin.data.User
import java.security.MessageDigest

class RegisterActivity : AppCompatActivity() {


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.register)


        // Find the UI elements by ID
        val emailInput: EditText = findViewById(R.id.emailInput)
        val usernameInput : EditText = findViewById(R.id.usernameInput)
        val passwordInput: EditText = findViewById(R.id.passwordInput)
        val signUpButton: Button = findViewById(R.id.signUpButton)

        val rep = Repository()

        signUpButton.setOnClickListener {
            val email = emailInput.text.toString().trim().lowercase()
            val username = usernameInput.text.toString().trim().lowercase()
            val password = passwordInput.text.toString().trim()
            val passhash = hashPass(password)

            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Preencha todos os campos!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            rep.createUser(User(email = email, username = username, hashPass = passhash, creditos = 500)) { user ->
                if (user != null) {
                    Toast.makeText(this, "Registro bem-sucedido!", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this, LoginActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Erro no registro!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun hashPass(input: String, algorithm: String = "SHA-256"): String {
        val bytes = MessageDigest.getInstance(algorithm).digest(input.toByteArray())
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }


}
