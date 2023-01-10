package ru.ertel.remotecontrole

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.ImageView

class MainActivity : AppCompatActivity() {

    private lateinit var imageView: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        imageView = findViewById(R.id.imageView)
        imageView.setImageResource(R.drawable.shlakclose)

        imageView.setOnClickListener {
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
}