package pt.ipt.dam.luckyspin.fragmentos

import android.content.Intent
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam.luckyspin.LoginActivity
import pt.ipt.dam.luckyspin.R
import pt.ipt.dam.luckyspin.RoletaActivity
import pt.ipt.dam.luckyspin.data.Repository
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader
import java.security.MessageDigest

class ProfileActivity: AppCompatActivity() {
    private lateinit var f1: tituloApp

    private lateinit var usrInput : EditText
    private lateinit var emailInput : EditText
    private lateinit var passInput : EditText
    private lateinit var passConfirm : EditText
    private lateinit var alterarDadosButton: Button
    private lateinit var btLogout: Button
    private lateinit var btEliminar: Button

    var lastEmail: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.profile)

        this.f1 = tituloApp.newInstance("", "");
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentAppTitle, f1)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        usrInput           = findViewById(R.id.usernameEditText)
        emailInput         = findViewById(R.id.emailEditText)
        passInput          = findViewById(R.id.passwordEditText)
        passConfirm        = findViewById(R.id.passwordNewEditText)
        alterarDadosButton = findViewById(R.id.alterarDadosButton)
        btLogout           = findViewById(R.id.btLogout)
        btEliminar         = findViewById(R.id.eliminarUser)

        val rep = Repository()

        var lastUsername = readFromFile("user.txt")
        usrInput.setText(lastUsername)

        rep.getUsers { users ->
            if (users != null) {
                val user = users.find { it.username == lastUsername }
                if (user != null) {
                    lastEmail = user.email
                    emailInput.setText(lastEmail)
                }
            } else {
                Toast.makeText(this, "Erro ao buscar usuários", Toast.LENGTH_SHORT).show()
            }
        }

        alterarDadosButton.setOnClickListener {

            lastUsername = readFromFile("user.txt")
            val creditStr = readFromFile("creditos.txt").toInt()

            if (passConfirm.getText().toString().isEmpty()){
                updateUser(lastUsername, usrInput.getText().toString(), emailInput.getText().toString(), passInput.getText().toString(), creditStr){ sucesso ->
                    if(sucesso){
                        Toast.makeText(this, "Dados atualizados!", Toast.LENGTH_SHORT).show()
                        writeToFile("user.txt", usrInput.getText().toString())
                    }
                }
            } else {
                updateUser(lastUsername, usrInput.getText().toString(), emailInput.getText().toString(), passInput.getText().toString(), creditStr){ sucesso ->
                    if(sucesso){
                        Toast.makeText(this, "Dados atualizados!", Toast.LENGTH_SHORT).show()
                        writeToFile("user.txt", usrInput.getText().toString())
                    }
                }
                lastUsername = readFromFile("user.txt")
                updateUserPass(lastUsername, passConfirm.getText().toString(), passInput.getText().toString()) { sucesso ->
                    if (sucesso) {
                        Toast.makeText(this, "Palavra-Passe atualizada!", Toast.LENGTH_SHORT).show()
                        passConfirm.setText("")
                        passInput.setText("")
                    }
                }
            }

        }

        btLogout.setOnClickListener {
            val username = readFromFile("user.txt")
            val creditStr = readFromFile("creditos.txt").toInt()
            updateUserCredits(username, creditStr) { sucesso ->
                if (sucesso) {
                    Toast.makeText(this, "Créditos atualizados!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(this, "Erro ao atualizar créditos", Toast.LENGTH_SHORT).show()
                }
            }

            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()

        }

        btEliminar.setOnClickListener {
            rep.getUsers { users ->
                if (users != null) {
                    val user = users.find { it.username == lastUsername }
                    if (user != null && user.id != null) {

                        if (hashPass(passInput.getText().toString()) == user.hashPass) {
                            rep.deleteUser(user.id) { success ->
                                if (success) {
                                    Toast.makeText(
                                        this,
                                        "Usuário excluído com sucesso!",
                                        Toast.LENGTH_SHORT
                                    ).show()

                                    val intent = Intent(this, LoginActivity::class.java)
                                    startActivity(intent)
                                    finish()

                                } else {
                                    Toast.makeText(
                                        this,
                                        "Erro ao excluir usuário",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                        }
                    } else {
                        Toast.makeText(this, "Erro ao buscar usuários", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

    }

    private fun readFromFile(fileName: String): String {
        return try {
            val fileInputStream = openFileInput(fileName)
            val inputStreamReader = InputStreamReader(fileInputStream)
            val bufferedReader = BufferedReader(inputStreamReader)
            val stringBuilder: StringBuilder = StringBuilder()
            bufferedReader.useLines { lines ->
                lines.forEach { stringBuilder.append(it).append("\n") }
            }
            stringBuilder.toString().trim()
        } catch (e: Exception) {
            e.printStackTrace()
            ""
        }
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

    private fun updateUserCredits(username: String, novosCreditos: Int, onResult: (Boolean) -> Unit) {
        val rep = Repository()
        rep.getUsers { users ->
            val user = users?.find { it.username == username }
            if (user != null && user.id != null) {
                val userAtualizado = user.copy(creditos = novosCreditos)
                rep.updateUser(user.id, userAtualizado) { updatedUser ->
                    if (updatedUser != null) {
                        Log.d("Update", "Créditos atualizados para $novosCreditos")
                        onResult(true)
                    } else {
                        Log.e("Update", "Erro ao atualizar créditos")
                        onResult(false)
                    }
                }
            } else {
                Log.e("Update", "Usuário não encontrado")
                onResult(false)
            }
        }
    }

    private fun updateUserPass(username: String, passNova: String, pass: String, onResult: (Boolean) -> Unit) {
        val rep = Repository()
        rep.getUsers { users ->
            val user = users?.find { it.username == username }
            if (user != null && user.id != null) {
                if(hashPass(pass) == user.hashPass){
                    val userAtualizado = user.copy(hashPass = hashPass(passNova))
                    rep.updateUser(user.id, userAtualizado) { updatedUser ->
                        if (updatedUser != null) {
                            onResult(true)
                        } else {
                            onResult(false)
                        }
                    }
                }
            } else {
                onResult(false)
            }
        }
    }

    private fun updateUser(lastUsername: String, usernameNew: String, emailnovo: String?, pass: String, novosCreditos: Int, onResult: (Boolean) -> Unit) {
        val rep = Repository()
        rep.getUsers { users ->
            val user = users?.find { it.username == lastUsername }
            if (user != null && user.id != null) {

                if(pass.isNotEmpty()){

                    if(hashPass(pass) == user.hashPass){
                        val userAtualizado = user.copy(username = usernameNew, email = emailnovo, creditos = novosCreditos)
                        rep.updateUser(user.id, userAtualizado) { updatedUser ->
                            if (updatedUser != null) {
                                onResult(true)
                            } else {
                                onResult(false)
                            }
                        }
                    } else {
                        Toast.makeText(this, "Palavra-Passe Incorreta!", Toast.LENGTH_SHORT).show()
                    }

                } else {
                    Toast.makeText(this, "Insira a sua Palavra-Passe para alterar os seus dados!", Toast.LENGTH_SHORT).show()
                }


            } else {
                onResult(false)
            }
        }
    }

    fun hashPass(input: String, algorithm: String = "SHA-256"): String {
        val bytes = MessageDigest.getInstance(algorithm).digest(input.toByteArray())
        return Base64.encodeToString(bytes, Base64.NO_WRAP)
    }

}

