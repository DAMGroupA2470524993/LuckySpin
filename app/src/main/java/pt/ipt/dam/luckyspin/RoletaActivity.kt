package pt.ipt.dam.luckyspin

import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam.luckyspin.fragmentos.tituloApp
import kotlin.random.Random

class RoletaActivity : AppCompatActivity(), SensorEventListener {

    lateinit var f1: tituloApp

    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0
    private var shakeThreshold = 12.0f

    private lateinit var rouletteWheel: ImageView
    private lateinit var resultText: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.roleta) // Make sure roleta.xml exists for the layout
        
        f1 = tituloApp.newInstance("", "");
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentAppTitle, f1)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        // Initialize UI components
        rouletteWheel = findViewById(R.id.rouletteWheel)
        resultText = findViewById(R.id.resultText)

        // Sensor setup
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer == null) {
            resultText.text = "No accelerometer available on this device."
        } else {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }
    }

    override fun onSensorChanged(event: SensorEvent?) {
        if (event == null) return

        val x = event.values[0]
        val y = event.values[1]
        val z = event.values[2]
        val gForce = Math.sqrt((x * x + y * y + z * z).toDouble()).toFloat()
        val currentTime = System.currentTimeMillis()

        if (gForce > shakeThreshold && currentTime - lastShakeTime > 500) {
            lastShakeTime = currentTime
            spinRoulette()
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}

    private fun spinRoulette() {
        val randomAngle = Random.nextInt(360, 7200) // Large number for multiple spins
        val animation = RotateAnimation(
            0f, randomAngle.toFloat(),
            Animation.RELATIVE_TO_SELF, 0.5f,
            Animation.RELATIVE_TO_SELF, 0.5f
        ).apply {
            duration = 3000 // Spin duration
            fillAfter = true
        }

        // Start animation
        rouletteWheel.startAnimation(animation)

        // Show result after spin
        rouletteWheel.postDelayed({
            val result = (randomAngle % 37) // Roulette has 37 slots (0-36)
            resultText.text = "Result: $result"
        }, 3000)
    }

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }
}
