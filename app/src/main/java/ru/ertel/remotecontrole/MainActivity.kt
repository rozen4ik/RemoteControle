package ru.ertel.remotecontrole

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.media.MediaPlayer
import android.os.*
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView
import android.widget.Toast
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import ru.ertel.remotecontrole.StartActivity.Companion.SAVE_TOKEN
import ru.ertel.remotecontrole.controller.KonturController
import ru.ertel.remotecontrole.data.DataSourceCatalogPackage

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.shlakclose)

        val settingsIdent: SharedPreferences = getSharedPreferences("ident", MODE_PRIVATE)
        val identClient = settingsIdent.getString(SAVE_TOKEN, "no").toString()

        val settingsIdentDevice: SharedPreferences = getSharedPreferences("identDevice", MODE_PRIVATE)
        val identDevice = settingsIdentDevice.getString(SAVE_TOKEN, "no").toString()

        val settingsURL: SharedPreferences = getSharedPreferences("url", MODE_PRIVATE)
        val urlKontur = "http://${settingsURL.getString(SAVE_TOKEN, "no").toString()}/monitor?script=True"
        val url = "http://${settingsURL.getString(SAVE_TOKEN, "no").toString()}/spd-xml-api"

        val konturController = KonturController()
        val dataSourceCatalogPackage = DataSourceCatalogPackage()

        val messageBlockDevice = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<command name=\"cLockDevice\" device=\"$identDevice\" guid=\"95D454F3-8EBD-50E6-A085-4E644468B8D6\"> " +
                "<param name=\"cpLocker\">Карта Тройка</param> " +
                "<param name=\"cpDuration\">30000</param> " +
                "<param name=\"cpSession\">85D323F3-8EBD-48E6-A085-4E652468B8D6</param> " +
                "</command> " +
                "</script>"
        // 1 - Вход, 2 - выход для cpDirection
        var messagePassageCard = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                "<script session=\"85D323F3-8EBD-48E6-A085-4E652468B8D6\"> " +
                "<command name=\"cRequest\" device=\"$identDevice\" guid=\"44871464-8EBD-56E6-A085-4E654768B8D6\"> " +
                "<param name=\"cpCard\">$identClient</param> " +
                "<param name=\"cpCardType\">1</param> " +
                "<param name=\"cpDirection\">2</param> " +
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
            if (imageView.tag == "close") {
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
//                    if (dataSourceCatalogPackage.getPassageCard().solution == "Пиратская копия") {
//                        val intent = Intent(this@MainActivity, LicenseActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    } else {
//                        bundle.putString(
//                            "deviceName",
//                            dataSourceCatalogPackage.getPassageCard().deviceName
//                        )
//                        bundle.putString("requestPassage", resultScanInfoCard)
//                        bundle.putString("solution", dataSourceCatalogPackage.getPassageCard().solution)
//                        bundle.putString("capt", dataSourceCatalogPackage.getPassageCard().capt)
//                        bundle.putString(
//                            "numberOfPasses",
//                            dataSourceCatalogPackage.getPassageCard().numberOfPasses
//                        )
//                        bundle.putString(
//                            "datePasses",
//                            dataSourceCatalogPackage.getPassageCard().datePasses
//                        )
//                        bundle.putString(
//                            "passageBalance",
//                            dataSourceCatalogPackage.getPassageCard().passageBalance
//                        )
//                        passageCardFragment.arguments = bundle
//                        openFragment(passageCardFragment)
//                    }
                    Handler().postDelayed(Runnable {
                        imageView.setImageResource(R.drawable.shlakopen)
                    }, 300)
                }, 300)
            } else {
                imageView.tag = "close"
                Handler().postDelayed(Runnable {
                    imageView.setImageResource(R.drawable.shlakmiddle)
                    Handler().postDelayed(Runnable {
                        imageView.setImageResource(R.drawable.shlakclose)
                    }, 300)
                }, 300)
            }
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
                    Log.d("TAG", konturController.requestPOST(urlPassage, messageBlockDevice))
                    var msg = konturController.requestPOST(urlPassage, messagePassageCard)
                    Log.d("TAG", msg)
                    dataSourceCatalogPackage.setMessagePassageCard(msg)
                    msg = msg.substringAfter("<Message>")
                    msg = msg.substringBefore("</Message>")
                    msg = msg.replace("rPrior", "rFinal")
//                    msg = msg.replace("rAlert", "rGrant")
                    msg = "<?xml version=\"1.0\" encoding=\"Windows-1251\"?> " +
                            "<script>" +
                            "<Message>$msg</Message>" +
                            "</script>"
                    Log.d("TAG", msg)
                    Log.d("TAG", konturController.requestPOST(urlPassage, msg))
                    dataSourceCatalogPackage.setAnswerDevice(
                        konturController.requestPOST(
                            urlPassage,
                            answerDevice,
                        )
//                        numberKontur
                    )
                    Log.d("TAG", konturController.requestPOST(urlPassage, messageUnBlockDevice))
//                    dataSourceCatalogPackage.setInfoCard(
//                        konturController.requestPOST(
//                            url,
//                            messageInfoCard
//                        )
//                        numberKontur
//                    )
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}