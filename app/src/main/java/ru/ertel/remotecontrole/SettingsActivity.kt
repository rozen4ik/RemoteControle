package ru.ertel.remotecontrole

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem

class SettingsActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)
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