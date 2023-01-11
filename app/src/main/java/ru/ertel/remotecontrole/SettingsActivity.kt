package ru.ertel.remotecontrole

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.TextView
import ru.ertel.remotecontrole.StartActivity.Companion.SAVE_TOKEN

class SettingsActivity : AppCompatActivity() {

    private lateinit var textDateLicense: TextView
    private lateinit var textIdent: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        textDateLicense = findViewById(R.id.textDateLicense)
        textIdent = findViewById(R.id.textIdent)

        val settingsDate: SharedPreferences = getSharedPreferences("endDate", MODE_PRIVATE)
        val settingsIdent: SharedPreferences = getSharedPreferences("ident", MODE_PRIVATE)
        val bodyDate = settingsDate.getString(SAVE_TOKEN, "no").toString()
        val bodyIdent = settingsIdent.getString(SAVE_TOKEN, "no").toString()

        textDateLicense.text = "Лицензия действует до $bodyDate\nОбновить лицензию:"
        textIdent.text = "Идентификатор клиента $bodyIdent\nВвести другой идентификатор клиента:"
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
}