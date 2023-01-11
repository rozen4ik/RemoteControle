package ru.ertel.remotecontrole

import android.content.Context
import android.content.Intent
import android.media.MediaPlayer
import android.os.*
import androidx.appcompat.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.shlakclose)

        imageView.setOnClickListener {
            vibroFone()
            if (imageView.tag == "close") {
                imageView.tag = "open"
                Handler().postDelayed(Runnable {
                    imageView.setImageResource(R.drawable.shlakmiddle)
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
}