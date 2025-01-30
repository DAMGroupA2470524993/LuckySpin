package pt.ipt.dam.luckyspin

import android.annotation.SuppressLint
import android.content.Context
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
import android.os.VibrationEffect
import android.os.Vibrator
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
import androidx.appcompat.app.AppCompatDelegate

class RoletaActivity : AppCompatActivity(), SensorEventListener  {
    private lateinit var f1: tituloApp

    private lateinit var image: ImageView
    private lateinit var roulette: ImageView

    private lateinit var btBlack : Button
    private lateinit var btRed : Button
    private lateinit var btGreen : Button
    private lateinit var btSpin : Button
    private lateinit var btChangeSensor : Button
    private lateinit var betButtons : List<Button>

    private var betBlack: Boolean = false
    private var betRed: Boolean = false
    private var betGreen: Boolean = false
    private var selectBt: Button? = null

    private var acOn: Boolean = true

    private lateinit var sensorManager: SensorManager
    private var vibrator: Vibrator? = null

    @SuppressLint("NewApi", "WrongThread", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.roleta)
        
        this.f1 = tituloApp.newInstance("", "");
        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.fragmentAppTitle, f1)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()

        /*                 SENSOR ACELERÓMETRO ERRO!!!!*/
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO) // garante que não está no dark mode
        setUpSensor()


        //Mostrar Gif para girar a roleta
        val source :ImageDecoder.Source = ImageDecoder.createSource(resources, R.drawable.spin_win)
        val drawable : Drawable = ImageDecoder.decodeDrawable(source)
        image = findViewById(R.id.imageViewResult)
        image.setImageDrawable(drawable)
        (drawable as? AnimatedImageDrawable)?.start()

        //Mostrar o Gif da roleta
        val sourceR :ImageDecoder.Source = ImageDecoder.createSource(resources, R.drawable.roleta_gif)
        val drawableR : Drawable = ImageDecoder.decodeDrawable(sourceR)
        roulette = findViewById(R.id.rouletteWheel)
        roulette.setImageDrawable(drawableR)
        (drawableR as? AnimatedImageDrawable)?.start()

        //identificação dos botões
         btBlack       = findViewById(R.id.btBlack)
         btRed         = findViewById(R.id.btRed)
         btGreen       = findViewById(R.id.btGreen)
         btSpin         = findViewById(R.id.btSpin)
         btChangeSensor = findViewById(R.id.btAc)

        //lista dos botões de apostas
        val betButtons = listOf(btBlack, btRed, btGreen)
        //função que permite bloquear os restantes botões quando um é acionado
        fun betButtonSelected(bt: Button) {
            if (bt == selectBt) {
                betButtons.forEach { it.isEnabled = true }
                selectBt = null
            } else {
                betButtons.forEach { it.isEnabled = it == bt }
                selectBt = bt
            }
        }


        //Quando carregado no botão, implementa a função betButtonSelected(bt: Button) e muda a cor
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
        btChangeSensor.setOnClickListener{
            if(acOn == true){
                acOn = false
                btChangeSensor.setBackgroundColor(Color.RED)
                btChangeSensor.text = "Sensor OFF"
            } else {
                acOn = true
                btChangeSensor.setBackgroundColor(Color.GREEN)
                btChangeSensor.text = "Sensor ON"
            }
        }

        //Quando carregado no botão, gira a roleta e verifica se venceu
        btSpin.setOnClickListener{
            //gerar um número entre 1 e 10
            //1, 3, 6, 8 - > preto
            //2, 4, 7, 9 - > vermelho
            //5, 10      - > verde

            if (acOn == false) {
                var randNum = Random.nextInt(10) + 1 // valores entre 1 e 7
                if (betBlack == false && betRed == false && betGreen == false){
                    Toast.makeText(this, "Please make a bet!", Toast.LENGTH_SHORT).show()
                } else {
                    spinRoulette(randNum)
                    checkWin(checkColor(randNum))
                }

            }

        }
    }

    //função que dá reset a todas as variáveis que sejam botões
    private fun resetBt() {
        betButtons.forEach { it.isEnabled = true }
        selectBt = null
        betBlack = false
        betRed = false
        betGreen = false
        btBlack.setBackgroundColor(Color.BLACK)
        btRed.setBackgroundColor(Color.RED)
        btGreen.setBackgroundColor(Color.GREEN)
    }

    /*                ROLETA                               */

    private fun spinRoulette(n : Int) {
        var imageToRoulette = when (n) {
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
        roulette.setImageResource(imageToRoulette)
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
            shake(1500, 50)
        } else {
                if (str == "verde" && betGreen == true){
                    val sourceRes :ImageDecoder.Source = ImageDecoder.createSource(
                        resources, R.drawable.jackpot
                    )
                    val drawableRes : Drawable = ImageDecoder.decodeDrawable(sourceRes)
                    image.setImageDrawable(drawableRes)
                    (drawableRes as? AnimatedImageDrawable)?.start()
                    shake(3000, 200)
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


     /*                 SENSOR ACELERÓMETRO                     */

    private fun setUpSensor() {
        sensorManager = getSystemService(SENSOR_SERVICE) as SensorManager
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.also { sensor ->
            if (acOn) {
                sensorManager.registerListener(
                    this,
                    sensor,
                    SensorManager.SENSOR_DELAY_UI, // Reduz chamadas desnecessárias
                    SensorManager.SENSOR_DELAY_UI
                )
            } else {
                sensorManager.unregisterListener(this) // Desativa o sensor quando acOn é falso
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.P)
    override fun onSensorChanged(event: SensorEvent?) {
        if (!acOn || event?.sensor?.type != Sensor.TYPE_ACCELEROMETER) return

        val sides = event.values[0]
        val upDown = event.values[1]

        if ((sides > 15 || sides < -15 || upDown > 15 || upDown < -15) && System.currentTimeMillis() - lastSensorTrigger > 1000) {
            lastSensorTrigger = System.currentTimeMillis()

            if (!betBlack && !betRed && !betGreen) {
                Toast.makeText(this, "Please make a bet!", Toast.LENGTH_SHORT).show()
            } else {
                val randNum = Random.nextInt(10) + 1
                spinRoulette(randNum)
                checkWin(checkColor(randNum))

            }

        }
    }

    // Variável global para evitar múltiplos acionamentos em sequência
    private var lastSensorTrigger: Long = 0

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {return}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)
    }

    private fun shake(millis: Long, intensity: Int) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(millis, intensity))
        }
    }
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