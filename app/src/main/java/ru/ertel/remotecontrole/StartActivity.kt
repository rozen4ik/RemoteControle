package ru.ertel.remotecontrole

import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import java.util.*

class StartActivity : AppCompatActivity() {

    companion object {
        const val SAVE_TOKEN = "save_token"
    }

    private lateinit var demoFragment: DemoFragment
    private lateinit var activateFragment: ActivateFragment
    private lateinit var dateNow: Calendar
    private lateinit var settingsToken: SharedPreferences
    private lateinit var settingsDate: SharedPreferences
    private lateinit var settingsIdent: SharedPreferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_start)

        activateFragment = ActivateFragment()
        demoFragment = DemoFragment()
        settingsToken = getSharedPreferences("konturToken", MODE_PRIVATE)
        settingsDate = getSharedPreferences("endDate", MODE_PRIVATE)
        settingsIdent = getSharedPreferences("ident", MODE_PRIVATE)

        val date = settingsDate.getString(SAVE_TOKEN, "no")
        val token = settingsToken.getString(SAVE_TOKEN, "no")
        val ident = settingsIdent.getString(SAVE_TOKEN, "no")

        if ((date == "no") && (token == "no") && (ident == "no")) {
            openFragment(activateFragment)
        } else {
//      Месяц считается от 0 до 11, так как хранится в виде массива
            val dateYearEnd = date?.substringAfter("/")
            val dateDayOfYearEnd = date?.substringBefore("/")
            dateNow = Calendar.getInstance()
            val dateDayOfYearNow = dateNow.get(Calendar.DAY_OF_YEAR)
            val dateYearNow = dateNow.get(Calendar.YEAR)

            if (dateYearNow < dateYearEnd!!.toInt()) {
                val intent = Intent(this@StartActivity, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else if (dateYearNow == dateYearEnd.toInt()) {
                if (dateDayOfYearNow < dateDayOfYearEnd!!.toInt()) {
                    val intent = Intent(this@StartActivity, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    openFragment(demoFragment)
                }
            } else {
                openFragment(demoFragment)
            }
        }
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.placeDateFragments, fragment)
            .commit()
    }
}