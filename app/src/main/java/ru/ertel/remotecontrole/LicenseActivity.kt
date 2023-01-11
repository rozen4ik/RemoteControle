package ru.ertel.remotecontrole

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import java.util.*

class LicenseActivity : AppCompatActivity() {

    private lateinit var bundle: Bundle
    private lateinit var pirateFragment: PirateFragment

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_license)

        bundle = Bundle()
        pirateFragment = PirateFragment()

        bundle.putString("pirate", "Вы используете пиратскую версию программы!")
        pirateFragment.arguments = bundle
        openFragment(pirateFragment)
    }

    private fun openFragment(fragment: Fragment) {
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.placePirateFragments, fragment)
            .commit()
    }
}