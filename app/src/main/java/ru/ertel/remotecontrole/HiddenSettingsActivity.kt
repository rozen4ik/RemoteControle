package ru.ertel.remotecontrole

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

class HiddenSettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_hidden_settings)
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