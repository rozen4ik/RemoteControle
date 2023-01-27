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
import ru.ertel.remotecontrole.data.DataSourceCatalogPackage

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView
    private lateinit var statusCheck: CheckBox
    private var status = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.shlakclose)

        statusCheck = findViewById(R.id.statusCheck)

        val settingsStatus: SharedPreferences = getSharedPreferences("status", MODE_PRIVATE)
        val bodyStatus = settingsStatus.getString(SAVE_TOKEN, "no").toString()

        val settingsIdent: SharedPreferences = getSharedPreferences("ident", MODE_PRIVATE)
        val identClient = settingsIdent.getString(SAVE_TOKEN, "no").toString()

        val settingsIdentDevice: SharedPreferences =
            getSharedPreferences("identDevice", MODE_PRIVATE)
        val identDevice = settingsIdentDevice.getString(SAVE_TOKEN, "no").toString()

        val settingsURL: SharedPreferences = getSharedPreferences("url", MODE_PRIVATE)
        val urlKontur =
            "http://${settingsURL.getString(SAVE_TOKEN, "no").toString()}/monitor?script=True"
        val url = "http://${settingsURL.getString(SAVE_TOKEN, "no").toString()}/spd-xml-api"

        val konturController = KonturController()
        val dataSourceCatalogPackage = DataSourceCatalogPackage()

        val onStatus = "Въезд на территорию"
        val noStatus = "Выезд с территории"
        var messagePassageCard = ""
        var cpDirection: Int = 1

        when (bodyStatus) {
            noStatus -> {
                statusCheck.isChecked = true
                statusCheck.text = noStatus
                cpDirection = 2
                messagePassageCard = messagePassageCard.replace(
                    "<param name=\"cpDirection\">1</param>",
                    "<param name=\"cpDirection\">2</param>"
                )
            }
            "no" -> {
                statusCheck.isChecked = false
                statusCheck.text = onStatus
                cpDirection = 1
                messagePassageCard = messagePassageCard.replace(
                    "<param name=\"cpDirection\">2</param>",
                    "<param name=\"cpDirection\">1</param>"
                )
                val saveStatus: SharedPreferences.Editor = settingsStatus.edit()
                saveStatus.putString(SAVE_TOKEN, onStatus)
                saveStatus.commit()
            }
            else -> {
                statusCheck.isChecked = false
                statusCheck.text = onStatus
                cpDirection = 1
                messagePassageCard = messagePassageCard.replace(
                    "<param name=\"cpDirection\">2</param>",
                    "<param name=\"cpDirection\">1</param>"
                )
            }
        }

        statusCheck.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val saveStatus: SharedPreferences.Editor = settingsStatus.edit()
                saveStatus.putString(SAVE_TOKEN, noStatus)
                saveStatus.commit()
                cpDirection = 2
                messagePassageCard = messagePassageCard.replace(
                    "<param name=\"cpDirection\">1</param>",
                    "<param name=\"cpDirection\">2</param>"
                )
                statusCheck.text = noStatus
            } else {
                val saveStatus: SharedPreferences.Editor = settingsStatus.edit()
                saveStatus.putString(SAVE_TOKEN, onStatus)
                saveStatus.commit()
                cpDirection = 1
                messagePassageCard = messagePassageCard.replace(
                    "<param name=\"cpDirection\">2</param>",
                    "<param name=\"cpDirection\">1</param>"
                )
                statusCheck.text = onStatus
            }
        }

        val messageBlockDevice = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<command name=\"cLockDevice\" device=\"$identDevice\" guid=\"95D454F3-8EBD-50E6-A085-4E644468B8D6\"> " +
                "<param name=\"cpLocker\">Карта Тройка</param> " +
                "<param name=\"cpDuration\">30000</param> " +
                "<param name=\"cpSession\">85D323F3-8EBD-48E6-A085-4E652468B8D6</param> " +
                "</command> " +
                "</script>"
        // 1 - Вход, 2 - выход для cpDirection
        messagePassageCard = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<command name=\"cRequest\" device=\"$identDevice\" guid=\"44871464-8EBD-56E6-A085-4E654768B8D6\"> " +
                "<param name=\"cpCard\">$identClient</param> " +
                "<param name=\"cpCardType\">1</param> " +
                "<param name=\"cpDirection\">$cpDirection</param> " +
                "<param name=\"cpText\">Запрос по карте</param> " +
                "</command> " +
                "</script>"
        val messageUnBlockDevice = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<command name=\"cUnlockDevice\" device=\"$identDevice\" guid=\"98545167-8EBD-6578-A085-4E633368B8D6\"> " +
                "<param name=\"cpLocker\">Карта Тройка</param> " +
                "<param name=\"cpDuration\">30000</param> " +
                "<param name=\"cpSession\">85D323F3-8EBD-48E6-A085-4E652468B8D6</param> " +
                "</command> " +
                "</script>"
        val answerDevice = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<wait delay=\"20000\" device=\"$identDevice\"/> " +
                "</script>"

        imageView.setOnClickListener {
            vibroFone()
            imageView.tag = "open"
            Handler().postDelayed(Runnable {
                imageView.setImageResource(R.drawable.shlakmiddle)
                updatePassageCard(
                    konturController,
                    dataSourceCatalogPackage,
                    urlKontur,
                    url,
                    messageBlockDevice,
                    messagePassageCard,
                    answerDevice,
                    messageUnBlockDevice
//                        numberKontur,
                )
                Handler().postDelayed(Runnable {
                    imageView.setImageResource(R.drawable.shlakopen)
                    Toast.makeText(this, status, Toast.LENGTH_LONG).show()
                    imageView.tag = "close"
                    Handler().postDelayed(Runnable {
                        imageView.setImageResource(R.drawable.shlakclose)
                    }, 1500)
                }, 300)
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

    private fun updateMsgPassageCard(msg: String): String {
        var message = msg
        message = if (message.contains("<param name=\"cpDirection\">1</param>")) {
            message.replace(
                "<param name=\"cpDirection\">1</param>",
                "<param name=\"cpDirection\">2</param>"
            )
        } else {
            message.replace(
                "<param name=\"cpDirection\">2</param>",
                "<param name=\"cpDirection\">1</param>"
            )
        }
        return message
    }

    private fun updatePassageCard(
        konturController: KonturController,
        dataSourceCatalogPackage: DataSourceCatalogPackage,
        urlPassage: String,
        url: String,
        messageBlockDevice: String,
        messagePassageCard: String,
        answerDevice: String,
        messageUnBlockDevice: String
//        messageInfoCard: String
//        numberKontur: String

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
                    dataSourceCatalogPackage.setMessagePassageCard(msg)
                    msg = msg.substringAfter("<Message>")
                    msg = msg.substringBefore("</Message>")
                    msg = msg.replace("rPrior", "rFinal")
                    msg = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                            "<script>" +
                            "<Message>$msg</Message>" +
                            "</script>"
                    Log.d("TAG", "MSGEdit\n$msg")
                    if (msg.contains("rGrant")) {
                        status = "Проход разрешён"
                    } else {
                        status = "Проход запрещён"
                    }
                    Log.d("TAG", konturController.requestPOST(urlPassage, msg))
                    var answerKontur = konturController.requestPOST(urlPassage, answerDevice)
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