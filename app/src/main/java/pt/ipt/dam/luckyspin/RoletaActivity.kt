package pt.ipt.dam.luckyspin

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.drawable.AnimatedImageDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Build
import android.os.Bundle
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import kotlin.random.Random
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatDelegate
import pt.ipt.dam.luckyspin.data.Repository
import pt.ipt.dam.luckyspin.fragmentos.ProfileActivity
import java.io.BufferedReader
import java.io.FileOutputStream
import java.io.InputStreamReader

class RoletaActivity : AppCompatActivity(), SensorEventListener  {

    private lateinit var image: ImageView
    private lateinit var roulette: ImageView

    private lateinit var userPlace: TextView
    private lateinit var cPlace: TextView
    private lateinit var profPic: ImageButton
    private lateinit var betValue: EditText

    private lateinit var btBlack : Button
    private lateinit var btRed : Button
    private lateinit var btGreen : Button
    private lateinit var btSpin : Button
    private lateinit var btHelp : ImageButton
    private lateinit var btSettings : ImageButton
    private lateinit var btChangeSensor : ImageButton
    private lateinit var btChangeVib : ImageButton
    private lateinit var betButtons : List<Button>

    private var betBlack: Boolean = false
    private var betRed: Boolean = false
    private var betGreen: Boolean = false
    private var selectBt: Button? = null

    private var defOn: Boolean = false
    private var acOn: Boolean = true
    private var vibOn: Boolean = true

    private lateinit var sensorManager: SensorManager
    private var vibrator: Vibrator? = null

    @SuppressLint("NewApi", "WrongThread", "SetTextI18n")
    @RequiresApi(Build.VERSION_CODES.P)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.roleta)


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
         btBlack        = findViewById(R.id.btBlack)
         btRed          = findViewById(R.id.btRed)
         btGreen        = findViewById(R.id.btGreen)
         btSpin         = findViewById(R.id.btSpin)
         btHelp         = findViewById(R.id.btHelp)
         btSettings     = findViewById(R.id.btDef)
         btChangeSensor = findViewById(R.id.btAc)
         btChangeVib    = findViewById(R.id.btVib)
         userPlace      = findViewById(R.id.usernamePlace)
         cPlace         = findViewById(R.id.userCredits)
         betValue       = findViewById(R.id.betValue)
         profPic        = findViewById(R.id.profilePicture)

        val bordaBl = GradientDrawable()
        val bordaRd = GradientDrawable()
        val bordaGn = GradientDrawable()


        userPlace.setText(readFromFile("user.txt"))
        cPlace.setText(readFromFile("creditos.txt"))

        //lista dos botões de apostas
        betButtons = listOf(btBlack, btRed, btGreen)
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

        //função que dá reset a todas as variáveis que sejam botões
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

        profPic.setOnClickListener{
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }
        //Quando carregado no botão, implementa a função betButtonSelected(bt: Button) e muda a cor
        btBlack.setOnClickListener{
            betButtonSelected(btBlack)
            if (betBlack == false) {
                betBlack = true
                bordaBl.setColor(Color.BLACK)
                bordaBl.setStroke(10, Color.WHITE)
                bordaBl.cornerRadius = 10f
                btBlack.background = bordaBl
            } else {
                betBlack = false
                bordaBl.setColor(Color.BLACK)
                bordaBl.setStroke(0, Color.WHITE)
                bordaBl.cornerRadius = 10f
                btBlack.background = bordaBl
            }
        }
        btRed.setOnClickListener{
            betButtonSelected(btRed)
            if (betRed == false) {
                betRed = true
                bordaRd.setColor(Color.RED)
                bordaRd.setStroke(10, Color.WHITE)
                bordaRd.cornerRadius = 10f
                btRed.background = bordaRd
            } else {
                betRed = false
                bordaRd.setColor(Color.RED)
                bordaRd.setStroke(0, Color.WHITE)
                bordaRd.cornerRadius = 10f
                btRed.background = bordaRd
            }
        }
        btGreen.setOnClickListener{
            betButtonSelected(btGreen)
            if (betGreen == false) {
                betGreen = true
                bordaGn.setColor(Color.GREEN)
                bordaGn.setStroke(10, Color.WHITE)
                bordaGn.cornerRadius = 10f
                btGreen.background = bordaGn
            } else {
                betGreen = false
                bordaGn.setColor(Color.GREEN)
                bordaGn.setStroke(0, Color.WHITE)
                bordaGn.cornerRadius = 10f
                btGreen.background = bordaGn
            }
        }

        btHelp.setOnClickListener{
            showInstructionsDialog()
        }

        btSettings.setOnClickListener{
            defOn = !defOn
            if (defOn == true) {
                btChangeSensor.visibility = View.VISIBLE
                btChangeVib.visibility = View.VISIBLE
            } else {
                btChangeSensor.visibility = View.INVISIBLE
                btChangeVib.visibility = View.INVISIBLE
            }
        }
        btChangeSensor.setOnClickListener{
            if(acOn == true){
                acOn = false
                Toast.makeText(this, "Acelerómetro Desligado!", Toast.LENGTH_SHORT).show()
                btSpin.visibility = View.VISIBLE
            } else {
                acOn = true
                Toast.makeText(this, "Acelerómetro Ligado!", Toast.LENGTH_SHORT).show()
                btSpin.visibility = View.INVISIBLE
            }
        }
        btChangeVib.setOnClickListener{
            if(vibOn == true){
                vibOn = false
                Toast.makeText(this, "Vibração Desligada!", Toast.LENGTH_SHORT).show()
            } else {
                vibOn = true
                Toast.makeText(this, "Vibração Ligada!", Toast.LENGTH_SHORT).show()
            }
        }

        //Quando carregado no botão, gira a roleta e verifica se venceu
        btSpin.setOnClickListener{
            //gerar um número entre 1 e 10
            //1, 3, 6, 8 - > preto
            //2, 4, 7, 9 - > vermelho
            //5, 10      - > verde

            if (acOn == false) {
                val bet = betValue.text.toString().toInt()
                var credits  = readFromFile("creditos.txt").toInt()
                if (betBlack == false && betRed == false && betGreen == false){
                        Toast.makeText(this, "Please make a bet!", Toast.LENGTH_SHORT).show()
                } else {
                    if (bet <= credits) {
                        credits -= bet
                        writeToFile("creditos.txt", credits.toString())
                        cPlace.setText(readFromFile("creditos.txt"))
                        var randNum = Random.nextInt(10) + 1
                        spinRoulette(randNum)
                        checkWin(checkColor(randNum), bet)
                    }  else {
                        Toast.makeText(this, "Não tens Creditos Suficientes", Toast.LENGTH_SHORT).show()
                    }

                }

            }
            resetBt()
        }
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
    private fun checkWin(str : String, betV: Int) {
        if ((str == "preto" && betBlack == true) || (str == "vermelho" && betRed == true)) {
            val sourceRes :ImageDecoder.Source = ImageDecoder.createSource(
                resources, R.drawable.win
            )
            val drawableRes : Drawable = ImageDecoder.decodeDrawable(sourceRes)
            image.setImageDrawable(drawableRes)
            (drawableRes as? AnimatedImageDrawable)?.start()
            if (vibOn == true) {
                shake(1500, 50)
            }
            var credits  = readFromFile("creditos.txt").toInt()
            credits += betV * 2
            writeToFile("creditos.txt", credits.toString())
            cPlace.setText(readFromFile("creditos.txt"))
        } else {
                if (str == "verde" && betGreen == true){
                    val sourceRes :ImageDecoder.Source = ImageDecoder.createSource(
                        resources, R.drawable.jackpot
                    )
                    val drawableRes : Drawable = ImageDecoder.decodeDrawable(sourceRes)
                    image.setImageDrawable(drawableRes)
                    (drawableRes as? AnimatedImageDrawable)?.start()
                    if (vibOn == true) {
                        shake(3000, 200)
                    }
                    var credits  = readFromFile("creditos.txt").toInt()
                    credits += betV * 5
                    writeToFile("creditos.txt", credits.toString())
                    cPlace.setText(readFromFile("creditos.txt"))
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

            val bet = betValue.text.toString().toInt()
             var credits  = readFromFile("creditos.txt").toInt()

                if (!betBlack && !betRed && !betGreen) {
                    Toast.makeText(this, "Please make a bet!", Toast.LENGTH_SHORT).show()
                } else {
                    if (bet <= credits) {
                        credits -= bet
                        writeToFile("creditos.txt", credits.toString())
                        cPlace.setText(readFromFile("creditos.txt"))
                        val randNum = Random.nextInt(10) + 1
                        spinRoulette(randNum)
                        checkWin(checkColor(randNum), bet)
                    } else {
                        Toast.makeText(this, "Não tens Creditos Suficientes", Toast.LENGTH_SHORT).show()
                    }
                }



        }
    }

    // Variável global para evitar múltiplos acionamentos em sequência
    private var lastSensorTrigger: Long = 0

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {return}

    override fun onDestroy() {
        super.onDestroy()
        sensorManager.unregisterListener(this)

        val username = readFromFile("user.txt")
        val creditStr  = readFromFile("creditos.txt").toInt()
        updateUserCredits(username, creditStr){ sucesso ->
            if (sucesso) {
                Toast.makeText(this, "Créditos atualizados!", Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, "Erro ao atualizar créditos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun shake(millis: Long, intensity: Int) {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator?.vibrate(VibrationEffect.createOneShot(millis, intensity))
        }
    }

    private fun showInstructionsDialog() {
        val builder = AlertDialog.Builder(this)

        builder.setTitle("Como Jogar!")
            .setMessage("Como Jogar! \n\n" +
                    "1. Escolha quanto quer apostar.\n" +
                    "2. Faça uma aposta selecionando uma das três cores (preto, vermelho, verde).\n" +
                    "3. Agite o telemóvel.\n" +
                    "4. Veja o resultado!!\n\n" +
                    "NÃO SE ESQUEÇA DE TERMINAR SESSÃO PARA GUARDAR OS SEUS CRÉDITOS!\n " +
                    "Caso contrário o seus créditos não serão atualizados para a próxma vez que jogar\n\n" +
                    "Pode desativar a vibração e o acelerómetro nas definições")
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()

    }

    fun readFromFile(fileName: String): String {
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

    fun updateUserCredits(username: String, novosCreditos: Int, onResult: (Boolean) -> Unit) {

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


}




