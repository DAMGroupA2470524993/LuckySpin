package pt.ipt.dam.luckyspin

import android.annotation.SuppressLint
import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.view.animation.Animation
import android.view.animation.RotateAnimation
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import pt.ipt.dam.luckyspin.fragmentos.tituloApp
import kotlin.random.Random
import android.widget.Toast
import androidx.annotation.RequiresApi

class RoletaActivity : AppCompatActivity() {
    lateinit var f1: tituloApp

    lateinit var image: ImageView
    lateinit var btImage: ImageButton


    private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0
    private var shakeThreshold = 12.0f



    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.roleta)
        
        f1 = tituloApp.newInstance("", "");
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentAppTitle, f1)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        //iniciar utilizador
        val user = User()


        //Mostrar o Gif com animação
        val source :ImageDecoder.Source = ImageDecoder.createSource(
            resources, R.drawable.spin_win
        )
        val drawable : Drawable = ImageDecoder.decodeDrawable(source)
        image = findViewById(R.id.imageViewResult)
        image.setImageDrawable(drawable)
        (drawable as? AnimatedImageDrawable)?.start()

        //botão imagem da roleta
        btImage = findViewById(R.id.rouletteWheel)
        btImage.setOnClickListener {

            //verifica se os creditos são maiores ou iguais aos créditos
            if (){
                //caso seja maior ou igual
                //1- tiro o dinheiro
                //2- gira a rouleta
                spinRoulette()

            } else {
                //caso seja menor
                //Falta de créditos
                Toast.makeText(this,"Falta créditos!!",Toast.LENGTH_LONG).show()
            }


        }

        /*// Sensor setup
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer == null) {
            resultText.text = "No accelerometer available on this device."
        } else {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }*/
    }

    /*override fun onSensorChanged(event: SensorEvent?) {
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

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {}*/


    @SuppressLint("NewApi")
    @RequiresApi(Build.VERSION_CODES.P)
    private fun spinRoulette() {
       /* val randomAngle = Random.nextInt(360, 7200) // Large number for multiple spins
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
        }, 3000) */


        //COMO EU ACHO QUE FAZIAMOS QUE ERA MAIS SIMPLES
        //GERAR UM NÚMERO ATÉ 7 APENAS
        //1,3,5 -> GANHAVA
        //2,4,6 -> PERDIA
        //7 -> SUPER JACKPOT

        //1
        var randNum = Random.nextInt(7) + 1 // valores entre 1 e 7

        //Verifica o número Random e adequa o a imagem a aparecer
        if (randNum == 1 || randNum == 3 || randNum == 5 ){
            val source :ImageDecoder.Source = ImageDecoder.createSource(
                resources, R.drawable.win
            )
            val drawable : Drawable = ImageDecoder.decodeDrawable(source)
            image.setImageDrawable(drawable)
            (drawable as? AnimatedImageDrawable)?.start()
        } else {
            if (randNum == 2 || randNum == 4 || randNum == 6) {
                val source :ImageDecoder.Source = ImageDecoder.createSource(
                    resources, R.drawable.lose
                )
                val drawable : Drawable = ImageDecoder.decodeDrawable(source)
                image.setImageDrawable(drawable)
                (drawable as? AnimatedImageDrawable)?.start()
            } else {
                if (randNum == 7) {
                    val source: ImageDecoder.Source = ImageDecoder.createSource(
                        resources, R.drawable.jackpot
                    )
                    val drawable: Drawable = ImageDecoder.decodeDrawable(source)
                    image.setImageDrawable(drawable)
                    (drawable as? AnimatedImageDrawable)?.start()
                }
            }
        }



    }

   /* override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }*/
}

data class User(
    val id: Int,
    val email: String,
    val creditos: Int
){
    fun user(id: Int)
}
