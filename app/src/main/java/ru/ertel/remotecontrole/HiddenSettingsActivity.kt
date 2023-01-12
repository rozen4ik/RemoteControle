package ru.ertel.remotecontrole

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.TextView
import ru.ertel.remotecontrole.StartActivity.Companion.SAVE_TOKEN

class HiddenSettingsActivity : AppCompatActivity() {

    private lateinit var adminCheck: CheckBox
    private lateinit var statusAdmin: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hidden_settings)

        val settingsAdmin: SharedPreferences = getSharedPreferences("admin", MODE_PRIVATE)
        val bodyAdmin = settingsAdmin.getString(SAVE_TOKEN, "no").toString()

        val onAdmin = "Режим администратора активирован"
        val noAdmin = "Режим администратора не активирован"

        adminCheck = findViewById(R.id.adminCheck)
        statusAdmin = findViewById(R.id.statusAdmin)

        if (bodyAdmin == onAdmin) {
            adminCheck.isChecked = true
            statusAdmin.text = onAdmin
        } else {
            adminCheck.isChecked = false
            statusAdmin.text = noAdmin
        }

        adminCheck.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked) {
                val saveAdmin: SharedPreferences.Editor = settingsAdmin.edit()
                saveAdmin.putString(SAVE_TOKEN, onAdmin)
                saveAdmin.commit()

                statusAdmin.text = onAdmin
            } else {
                val saveAdmin: SharedPreferences.Editor = settingsAdmin.edit()
                saveAdmin.putString(SAVE_TOKEN, noAdmin)
                saveAdmin.commit()

                statusAdmin.text = noAdmin
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        menu?.add(Menu.NONE, 1, 0, "Главная")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_about -> {
                val intent = Intent(this@HiddenSettingsActivity, AboutActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            R.id.action_settings -> {
                val intent = Intent(this@HiddenSettingsActivity, SettingsActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            1 -> {
                val intent = Intent(this@HiddenSettingsActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item);
    }
}