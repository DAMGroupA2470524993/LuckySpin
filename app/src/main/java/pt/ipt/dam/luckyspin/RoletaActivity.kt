package pt.ipt.dam.luckyspin

import android.annotation.SuppressLint
import android.graphics.Color
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
import android.widget.Button
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
    lateinit var roulette: ImageView

    private var betBlack: Boolean = false
    private var betRed: Boolean = false
    private var betGreen: Boolean = false

    /*private lateinit var sensorManager: SensorManager
    private var accelerometer: Sensor? = null
    private var lastShakeTime: Long = 0
    private var shakeThreshold = 12.0f*/


    @SuppressLint("NewApi", "WrongThread")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.roleta)
        
        f1 = tituloApp.newInstance("", "");
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentAppTitle, f1)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()


        //Mostrar o Gif com animação
        val source :ImageDecoder.Source = ImageDecoder.createSource(
            resources, R.drawable.spin_win
        )
        val drawable : Drawable = ImageDecoder.decodeDrawable(source)
        image = findViewById(R.id.imageViewResult)
        image.setImageDrawable(drawable)
        (drawable as? AnimatedImageDrawable)?.start()

        //Mostrar o Gif com animação
        val sourceR :ImageDecoder.Source = ImageDecoder.createSource(
            resources, R.drawable.roleta_gif
        )
        val drawableR : Drawable = ImageDecoder.decodeDrawable(sourceR)
        roulette = findViewById(R.id.rouletteWheel)
        roulette.setImageDrawable(drawableR)
        (drawableR as? AnimatedImageDrawable)?.start()

        var btBlack : Button = findViewById(R.id.btBlack)
        var btRed : Button = findViewById(R.id.btRed)
        var btGreen : Button = findViewById(R.id.btGreen)
        var btSpin : Button = findViewById(R.id.btSpin)

        val betButtons = listOf(btBlack, btRed, btGreen)

        var selectBt: Button? = null

        fun betButtonSelected(bt: Button) {
            if (bt == selectBt) {
                betButtons.forEach { it.isEnabled = true }
                selectBt = null
            } else {
                betButtons.forEach { it.isEnabled = it == bt }
                selectBt = bt
            }
        }
        fun resetBt() {
            betButtons.forEach { it.isEnabled = true }
            selectBt = null
            betBlack = false
            betRed = false
            betGreen = false
            btBlack.setBackgroundColor(Color.BLACK)
            btRed.setBackgroundColor(Color.RED)
            btGreen.setBackgroundColor(Color.GREEN)
        }


        btBlack.setOnClickListener{
            betButtonSelected(btBlack)
            if (betBlack == false) {
                betBlack = true
                btBlack.setBackgroundColor(Color.BLUE)
            } else {
                betBlack = false
                btBlack.setBackgroundColor(Color.BLACK)
            }
        }
        btRed.setOnClickListener{
            betButtonSelected(btRed)
            if (betRed == false) {
                betRed = true
                btRed.setBackgroundColor(Color.BLUE)
            } else {
                betRed = false
                btRed.setBackgroundColor(Color.RED)
            }
        }
        btGreen.setOnClickListener{
            betButtonSelected(btGreen)
            if (betGreen == false) {
                betGreen = true
                btGreen.setBackgroundColor(Color.BLUE)
            } else {
                betGreen = false
                btGreen.setBackgroundColor(Color.GREEN)
            }
        }




        btSpin.setOnClickListener{
            //gerar um número entre 1 e 10
            //1, 3, 6, 8 - > preto
            //2, 4, 7, 9 - > vermelho
            //5, 10      - > verde

            var randNum = Random.nextInt(10) + 1 // valores entre 1 e 7

            if (betBlack == false && betRed == false && betGreen == false){
                Toast.makeText(this, "Please make a bet!", Toast.LENGTH_SHORT).show()
            } else {
                spinRoulette(randNum)
                checkColor(randNum)
                checkWin(checkColor(randNum))
            }
            resetBt()
        }

    }

    private fun spinRoulette(n : Int) {
        var imagetoRoulette = when (n) {
            1 -> R.drawable.roleta1
            2 -> R.drawable.roleta2
            3 -> R.drawable.roleta3
            4 -> R.drawable.roleta4
            5 -> R.drawable.roleta5
            6 -> R.drawable.roleta6
            7 -> R.drawable.roleta7
            8 -> R.drawable.roleta8
            9 -> R.drawable.roleta9
            10 -> R.drawable.roleta10
            else -> R.drawable.roleta
        }
        roulette.setImageResource(imagetoRoulette)
    }

    private fun checkColor(n : Int) : String {
        if (n == 1 || n == 3 || n == 6 || n == 8) {
            return "preto"
        } else{
            if (n == 2 || n == 4 || n == 7 || n == 9) {
                return "vermelho"
            } else {
                return "verde"
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun checkWin(str : String) {
        if ((str == "preto" && betBlack == true) || (str == "vermelho" && betRed == true)) {
            val sourceRes :ImageDecoder.Source = ImageDecoder.createSource(
                resources, R.drawable.win
            )
            val drawableRes : Drawable = ImageDecoder.decodeDrawable(sourceRes)
            image.setImageDrawable(drawableRes)
            (drawableRes as? AnimatedImageDrawable)?.start()
        } else {
                if (str == "verde" && betGreen == true){
                    val sourceRes :ImageDecoder.Source = ImageDecoder.createSource(
                        resources, R.drawable.jackpot
                    )
                    val drawableRes : Drawable = ImageDecoder.decodeDrawable(sourceRes)
                    image.setImageDrawable(drawableRes)
                    (drawableRes as? AnimatedImageDrawable)?.start()
                } else {
                        val sourceRes :ImageDecoder.Source = ImageDecoder.createSource(
                            resources, R.drawable.lose
                        )
                        val drawableRes : Drawable = ImageDecoder.decodeDrawable(sourceRes)
                        image.setImageDrawable(drawableRes)
                        (drawableRes as? AnimatedImageDrawable)?.start()
                }
            }
        }





 ///////////////////////////////////////////////////////////////
   /* override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }*/
}

//dentro do onCreate
/*// Sensor setup
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        if (accelerometer == null) {
            resultText.text = "No accelerometer available on this device."
        } else {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        }*/
////////////////////////////////////////////////
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