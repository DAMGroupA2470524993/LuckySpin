package pt.ipt.dam.luckyspin

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam.luckyspin.fragmentos.tituloAppWelcome

class MainActivity : AppCompatActivity() {

    lateinit var f1:tituloAppWelcome

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        f1 = tituloAppWelcome.newInstance("", "");
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentAppTitle, f1)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        // Handle SPIN button click
        val spinButton: Button = findViewById(R.id.startButton)
        spinButton.setOnClickListener {
            // Navigate to LoginActivity
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
