package com.dena.googlesignin

import kotlinx.android.synthetic.main.activity_google_calendar.*

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.Toolbar
import android.view.MenuItem
import android.support.v4.app.NavUtils
import android.content.Intent
import android.util.Log


class GoogleCalendarActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_google_calendar)

        val toolbar = supportActionBar
        toolbar?.setDisplayHomeAsUpEnabled(true)

        title = this.localClassName

        
    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        Log.d("onOptionsItemSelected", item?.itemId?.toString() ?: "")
        Log.d("R.id.home", R.id.homeAsUp.toString() )

        finish()

        when (item?.itemId) {
            R.id.home -> {
                val upIntent = NavUtils.getParentActivityIntent(this)
                NavUtils.navigateUpTo(this, upIntent)
            }
        }

        return super.onOptionsItemSelected(item)
    }
}
