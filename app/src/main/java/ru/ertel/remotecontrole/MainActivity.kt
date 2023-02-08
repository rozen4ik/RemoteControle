package ru.ertel.remotecontrole

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import ru.ertel.remotecontrole.StartActivity.Companion.SAVE_TOKEN
import ru.ertel.remotecontrole.controller.KonturController
import ru.ertel.remotecontrole.data.DataSourceDevice
import ru.ertel.remotecontrole.data.DataSourceLicense
import java.net.ConnectException

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var spinnerSelectDevice: Spinner
    private var status = ""
    private var answerCheckMessage = ""
    private var statusInternet = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.shlakclose)

        spinnerSelectDevice = findViewById(R.id.selectDevice)

        val settingsStatus: SharedPreferences = getSharedPreferences("status", MODE_PRIVATE)
        val bodyStatus = settingsStatus.getString(SAVE_TOKEN, "no").toString()

        val settingsIdent: SharedPreferences = getSharedPreferences("ident", MODE_PRIVATE)
        val identClient = settingsIdent.getString(SAVE_TOKEN, "no").toString()

        val settingsURL: SharedPreferences = getSharedPreferences("url", MODE_PRIVATE)
        val urlKontur =
            "http://${settingsURL.getString(SAVE_TOKEN, "no").toString()}/monitor?script=True"
        val url = "http://${settingsURL.getString(SAVE_TOKEN, "no").toString()}/spd-xml-api"

        val settingsToken: SharedPreferences = getSharedPreferences("konturToken", MODE_PRIVATE)
        val bodyToken = settingsToken.getString(SAVE_TOKEN, "no").toString()

        val settingsAdmin: SharedPreferences = getSharedPreferences("admin", MODE_PRIVATE)
        val bodyAdmin = settingsAdmin.getString(SAVE_TOKEN, "no").toString()

        val konturController = KonturController()
        val dataSourceLicense = DataSourceLicense()
        val dataSourceDevice = DataSourceDevice()

        val messageDevice = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>\n" +
                "<script/>"

        checkDevice(konturController, dataSourceDevice, urlKontur, messageDevice)

        if (statusInternet == "Неправильно указан порт и ip") {

            Toast.makeText(this, "Неправильно указан порт и ip", Toast.LENGTH_LONG).show()

            val intent = Intent(this@MainActivity, SettingsActivity::class.java)
            startActivity(intent)
            finish()
        }

        val checkMessage = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?>" +
                "<spd-xml-api>" +
                "<request version=\"1.1\" ruid=\"739F9F2B-AEDD-4D94-90FF-EB59A9A1FCF5\">" +
                "</request>" +
                "</spd-xml-api>"

        var messageBlockDevice = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<command name=\"cLockDevice\" device= guid=\"95D454F3-8EBD-50E6-A085-4E644468B8D6\"> " +
                "<param name=\"cpLocker\">Карта Тройка</param> " +
                "<param name=\"cpDuration\">30000</param> " +
                "<param name=\"cpSession\">85D323F3-8EBD-48E6-A085-4E652468B8D6</param> " +
                "</command> " +
                "</script>"

//        var adminMessageBlockDevice = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
//                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
//                "<command admin=\"True\" visible=\"True\" wait-answer=\"True\" name=\"cLockDevice\" device= guid=\"95D454F3-8EBD-50E6-A085-4E644468B8D6\"> " +
//                "<param name=\"cpLocker\">Админ</param> " +
//                "<param name=\"cpDuration\">30000</param> " +
//                "<param name=\"cpSession\">85D323F3-8EBD-48E6-A085-4E652468B8D6</param> " +
//                "</command> " +
//                "</script>"

        // 1 - Вход, 2 - выход для cpDirection
        var messagePassageCard = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<command name=\"cRequest\" device= guid=\"44871464-8EBD-56E6-A085-4E654768B8D6\"> " +
                "<param name=\"cpCard\">$identClient</param> " +
                "<param name=\"cpCardType\">1</param> " +
                "<param name=\"cpDirection\">1</param> " +
                "<param name=\"cpText\">Запрос по карте</param> " +
                "</command> " +
                "</script>"

//        // 1 - Вход, 2 - выход для cpDirection
//        var adminMessagePassageCard = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
//                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
//                "<command admin=\"True\" visible=\"True\" wait-answer=\"True\" name=\"cRequest\" device= guid=\"44871464-8EBD-56E6-A085-4E654768B8D6\"> " +
//                "<param name=\"cpDirection\">1</param> " +
//                "<param name=\"cpText\">Админ</param> " +
//                "</command> " +
//                "</script>"

        var messageUnBlockDevice = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<command name=\"cUnlockDevice\" device= guid=\"98545167-8EBD-6578-A085-4E633368B8D6\"> " +
                "<param name=\"cpLocker\">Карта Тройка</param> " +
                "<param name=\"cpDuration\">30000</param> " +
                "<param name=\"cpSession\">85D323F3-8EBD-48E6-A085-4E652468B8D6</param> " +
                "</command> " +
                "</script>"

//        var adminMessageUnBlockDevice = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
//                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
//                "<command admin=\"True\" visible=\"True\" wait-answer=\"True\" name=\"cUnlockDevice\" device= guid=\"98545167-8EBD-6578-A085-4E633368B8D6\"> " +
//                "<param name=\"cpLocker\">Админ</param> " +
//                "<param name=\"cpDuration\">30000</param> " +
//                "<param name=\"cpSession\">85D323F3-8EBD-48E6-A085-4E652468B8D6</param> " +
//                "</command> " +
//                "</script>"

        var answerDevice = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<wait delay=\"20000\" device= /> " +
                "</script>"

        val devices = dataSourceDevice.getDeviceArray().toArray().reversed()
        var strDevicesSpin = ""

        for (d in devices) {
            strDevicesSpin += "${d.toString().substringAfter(":")},"
        }

        strDevicesSpin = strDevicesSpin.substringBeforeLast(",")

        val devicesSpinArrayList = strDevicesSpin.split(",").toList() as ArrayList<String>
        val devicesSpin = devicesSpinArrayList.toArray()

        val adapter: ArrayAdapter<Any?> =
            ArrayAdapter<Any?>(this, android.R.layout.simple_spinner_item, devicesSpin)
//        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        adapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line)
        spinnerSelectDevice.adapter = adapter

        imageView.setOnClickListener {
            vibroFone()
            imageView.tag = "open"
            Handler().postDelayed(Runnable {
                imageView.setImageResource(R.drawable.shlakmiddle)
                checkLicense(konturController, dataSourceLicense, url, bodyToken, checkMessage)
                if (dataSourceLicense.getSolution() == "Пиратская копия") {
                    val intent = Intent(this@MainActivity, LicenseActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    var device = spinnerSelectDevice.selectedItem.toString()

                    for (d in devices) {
                        if (device == d.toString().substringAfter(":")) {
                            device = d.toString().substringBefore(":")

                            messageBlockDevice = messageBlockDevice.replace("device=", "device=\"$device\"")
                            messagePassageCard = messagePassageCard.replace("device=", "device=\"$device\"")
                            answerDevice = answerDevice.replace("device=", "device=\"$device\"")
                            messageUnBlockDevice = messageUnBlockDevice.replace("device=", "device=\"$device\"")

                            updatePassageCard(
                                konturController,
                                urlKontur,
                                messageBlockDevice,
                                messagePassageCard,
                                answerDevice,
                                messageUnBlockDevice,
                            )

                            messageBlockDevice = messageBlockDevice.replace("device=\"$device\"", "device=")
                            messagePassageCard = messagePassageCard.replace("device=\"$device\"", "device=")
                            answerDevice = answerDevice.replace("device=\"$device\"", "device=")
                            messageUnBlockDevice = messageUnBlockDevice.replace("device=\"$device\"", "device=")
                        }
                    }
                }
                if (status == "Проход разрешён") {
                    Handler().postDelayed(Runnable {
                        imageView.setImageResource(R.drawable.shlakopen)
                        Toast.makeText(this, status, Toast.LENGTH_LONG).show()
                        imageView.tag = "close"
                        Handler().postDelayed(Runnable {
                            imageView.setImageResource(R.drawable.shlakclose)
                        }, 1500)

                    }, 600)
                } else {
                    Toast.makeText(this, status, Toast.LENGTH_LONG).show()
                    imageView.tag = "close"
                    Handler().postDelayed(Runnable {
                        imageView.setImageResource(R.drawable.shlakclose)
                    }, 300)
                }

            }, 300)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                val intent = Intent(this@MainActivity, AboutActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            R.id.action_settings -> {
                val intent = Intent(this@MainActivity, SettingsActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private fun vibroFone() {
        val vibrator = this.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        val canVibrate: Boolean = vibrator.hasVibrator()
        val milliseconds = 300L
        if (canVibrate) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                // API 26
                vibrator.vibrate(
                    VibrationEffect.createOneShot(
                        milliseconds,
                        VibrationEffect.DEFAULT_AMPLITUDE
                    )
                )
            } else {
                // This method was deprecated in API level 26
                vibrator.vibrate(milliseconds)
            }
        }
    }

    private fun checkDevice(
        konturController: KonturController,
        dataSourceDevice: DataSourceDevice,
        url: String,
        message: String
    ) {
        runBlocking {
            launch(newSingleThreadContext("CheclLicense")) {
                try {
                    val answerMessage = konturController.requestPOST(url, message)
                    if (answerMessage.contains("<answer>")) {
                        dataSourceDevice.setDeviceArray(answerMessage)
                    } else {
                        statusInternet = answerMessage
                    }
                } catch (e: ConnectException) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun checkLicense(
        konturController: KonturController,
        dataSourceLicense: DataSourceLicense,
        url: String,
        bodyToken: String,
        checkMessage: String
    ) {
        runBlocking {
            launch(newSingleThreadContext("CheclLicense")) {
                try {
                    answerCheckMessage = konturController.requestPOST(url, checkMessage)
                    Log.d("TAG", answerCheckMessage)
                    dataSourceLicense.setMessageLicense(answerCheckMessage, bodyToken)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }

    private fun updatePassageCard(
        konturController: KonturController,
        urlPassage: String,
        messageBlockDevice: String,
        messagePassageCard: String,
        answerDevice: String,
        messageUnBlockDevice: String
    ) {
        runBlocking {
            launch(newSingleThreadContext("MyOwnThread")) {
                try {

                    Log.d(
                        "TAG",
                        "MessageBlockDevice\n${
                            konturController.requestPOST(
                                urlPassage,
                                messageBlockDevice
                            )
                        }"
                    )
                    var msgPassageCard = messagePassageCard
                    var msg = konturController.requestPOST(urlPassage, msgPassageCard)
                    Log.d("TAG", "MessagePassageCard\n$msg")
                    msg = msg.substringAfter("<Message>")
                    msg = msg.substringBefore("</Message>")
                    msg = msg.replace("rPrior", "rFinal")
                    msg = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                            "<script>" +
                            "<Message>$msg</Message>" +
                            "</script>"
                    Log.d("TAG", "MSGEdit\n$msg")
                    status = if (msg.contains("rGrant")) {
                        "Проход разрешён"
                    } else {
                        "Проход запрещён"
                    }
                    Log.d("TAG", konturController.requestPOST(urlPassage, msg))
                    val answerKontur = konturController.requestPOST(urlPassage, answerDevice)
                    Log.d("TAG", "Answer\n$answerKontur")
                    Log.d(
                        "TAG",
                        "MessageUnBlockDevice\n${
                            konturController.requestPOST(
                                urlPassage,
                                messageUnBlockDevice
                            )
                        }"
                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}