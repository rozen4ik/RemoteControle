package ru.ertel.remotecontrole

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView

class AboutActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about)

        var count = 0
        val imageView: ImageView = findViewById(R.id.logo)
        imageView.setOnClickListener {
            count++
            if (count == 5) {
                count = 0
                val intent = Intent(this@AboutActivity, HiddenSettingsActivity::class.java)
                startActivity(intent)
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menu?.add(Menu.NONE, 1, 2, "Главная")
        menu?.add(Menu.NONE, 2, 2, "Настройки")
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            1 -> {
                val intent = Intent(this@AboutActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
            2 -> {
                val intent = Intent(this@AboutActivity, SettingsActivity::class.java)
                startActivity(intent)
                finish()
                return true
            }
        }
        return super.onOptionsItemSelected(item);
    }
}