package ru.ertel.remotecontrole

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import kotlinx.coroutines.launch
import kotlinx.coroutines.newSingleThreadContext
import kotlinx.coroutines.runBlocking
import ru.ertel.remotecontrole.StartActivity.Companion.SAVE_TOKEN
import ru.ertel.remotecontrole.controller.KonturController
import ru.ertel.remotecontrole.data.DataSourceDevice
import ru.ertel.remotecontrole.data.DataSourceToken
import java.net.ConnectException
import java.text.SimpleDateFormat


class SettingsActivity : AppCompatActivity() {

    private lateinit var textDateLicense: TextView
    private lateinit var editLicense: EditText
    private lateinit var saveLicense: Button
    private lateinit var textIdent: TextView
    private lateinit var editIdentClient: EditText
    private lateinit var saveIdentClient: Button
    private lateinit var showURL: TextView
    private lateinit var editURLBody: EditText
    private lateinit var editURLPort: EditText
    private lateinit var buttonSaveIpPort: Button
    private lateinit var dataSourceToken: DataSourceToken
    private lateinit var konturController: KonturController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        textDateLicense = findViewById(R.id.textDateLicense)
        editLicense = findViewById(R.id.editLicense)
        saveLicense = findViewById(R.id.saveLicense)
        textIdent = findViewById(R.id.textIdent)
        editIdentClient = findViewById(R.id.editIdentClient)
        saveIdentClient = findViewById(R.id.saveIdentClient)
        showURL = findViewById(R.id.showURL)
        editURLBody = findViewById(R.id.editURLBody)
        editURLPort = findViewById(R.id.editURLPort)
        buttonSaveIpPort = findViewById(R.id.buttonSaveIpPort)

        dataSourceToken = DataSourceToken()
        konturController = KonturController()

        val settingsToken: SharedPreferences = getSharedPreferences("konturToken", MODE_PRIVATE)
        val endDate: SharedPreferences = getSharedPreferences("date", MODE_PRIVATE)
        val settingsDate: SharedPreferences = getSharedPreferences("endDate", MODE_PRIVATE)
        val settingsIdent: SharedPreferences = getSharedPreferences("ident", MODE_PRIVATE)
        val settingsURL: SharedPreferences = getSharedPreferences("url", MODE_PRIVATE)

        val bodyDate = endDate.getString(SAVE_TOKEN, "no").toString()
        val bodyIdent = settingsIdent.getString(SAVE_TOKEN, "no").toString()
        val bodyURL = settingsURL.getString(SAVE_TOKEN, "no").toString()

        val konturController = KonturController()

        textDateLicense.text = "Лицензия действует до $bodyDate\nОбновить лицензию:"
        textIdent.text = "Идентификатор клиента $bodyIdent\nВвести другой идентификатор клиента:"
        showURL.text = "Используется адрес: http://$bodyURL"

        saveLicense.setOnClickListener {
            val urlToken =
                "http://license.u1733524.isp.regruhosting.ru/api/tokens/${editLicense.text}"

            infoToken(konturController, dataSourceToken, urlToken)

            if (dataSourceToken.getToken().nameToken == editLicense.text.toString()) {

                val formDate = SimpleDateFormat("yyyy-MM-dd")
                val getEndDayOfYear = SimpleDateFormat("D")
                val currentDate = formDate.parse(dataSourceToken.getToken().endDate)
                val endDayOfYear = getEndDayOfYear.format(currentDate)
                val endYear = "${dataSourceToken.getToken().endDate[0]}" +
                        "${dataSourceToken.getToken().endDate[1]}" +
                        "${dataSourceToken.getToken().endDate[2]}" +
                        "${dataSourceToken.getToken().endDate[3]}"

                val saveEndDateToken: SharedPreferences.Editor = settingsDate.edit()
                saveEndDateToken.putString(SAVE_TOKEN, "$endDayOfYear/$endYear")
                saveEndDateToken.commit()

                val numberKontur = dataSourceToken.getToken().nameToken.substringAfterLast("*")
                val saveKonturToken: SharedPreferences.Editor = settingsToken.edit()
                saveKonturToken.putString(SAVE_TOKEN, numberKontur)
                saveKonturToken.commit()

                val saveEndDate: SharedPreferences.Editor = endDate.edit()
                saveEndDate.putString(SAVE_TOKEN, dataSourceToken.getToken().endDate)
                saveEndDate.commit()

                val upDate: SharedPreferences = getSharedPreferences("date", MODE_PRIVATE)
                val upBodyDate = upDate.getString(SAVE_TOKEN, "no").toString()
                textDateLicense.text = "Лицензия действует до $upBodyDate\nОбновить лицензию:"
            } else {
                Toast.makeText(this, "Введен некорректный ключ", Toast.LENGTH_LONG).show()
            }
        }
        saveIdentClient.setOnClickListener {
            val saveIdentClient: SharedPreferences.Editor = settingsIdent.edit()
            saveIdentClient.putString(SAVE_TOKEN, editIdentClient.text.toString())
            saveIdentClient.commit()

            val upIdent: SharedPreferences = getSharedPreferences("ident", MODE_PRIVATE)
            val upBodyIdent = upIdent.getString(SAVE_TOKEN, "no").toString()
            textIdent.text =
                "Идентификатор клиента $upBodyIdent\nВвести другой идентификатор клиента:"
        }

        buttonSaveIpPort.setOnClickListener {
            val saveURL: SharedPreferences.Editor = settingsURL.edit()
            saveURL.putString(SAVE_TOKEN, "${editURLBody.text}:${editURLPort.text}")
            saveURL.commit()

            val upURL: SharedPreferences = getSharedPreferences("url", MODE_PRIVATE)
            val upBodyURL = upURL.getString(SAVE_TOKEN, "no").toString()
            showURL.text = "Используется адрес: http://$upBodyURL"
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(Menu.NONE, 1, 2, "Главная")
        menu?.add(Menu.NONE, 2, 2, "Об приложении")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> {
                val intent = Intent(this@SettingsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            2 -> {
                val intent = Intent(this@SettingsActivity, AboutActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item);
    }

    private fun infoToken(
        konturController: KonturController,
        dataSourceToken: DataSourceToken,
        url: String
    ) {
        runBlocking {
            launch(newSingleThreadContext("MyOwnThread")) {
                try {
                    when (val message = konturController.requestGetToken(url)) {
                        "\"Превышено количество активаций\"" -> {
                            dataSourceToken.setAnswer(message)
                        }
                        "\"Отказано в активации, ключ не действительный\"" -> {
                            dataSourceToken.setAnswer(message)
                        }
                        else -> {
                            dataSourceToken.setAnswer("Одобрено")
                            dataSourceToken.setToken(message)
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        }
    }
}