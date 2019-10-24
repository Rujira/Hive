package com.codinghub.apps.hive.ui.settings

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.SeekBar
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProviders
import com.codinghub.apps.hive.R
import com.codinghub.apps.hive.model.login.UserRole
import com.codinghub.apps.hive.model.preferences.AppPrefs
import com.codinghub.apps.hive.viewmodel.SettingsViewModel
import kotlinx.android.synthetic.main.activity_settings.*

class SettingsActivity : AppCompatActivity() {

    private lateinit var settingsViewModel: SettingsViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        this.title = getString(R.string.menu_settings)

        settingsViewModel = ViewModelProviders.of(this).get(SettingsViewModel::class.java)

        assert(supportActionBar != null)   //null check

        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.elevation = 0.0f
        updateUI()

        notificationSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                settingsViewModel.registerUserForPushNotification(AppPrefs.getSchoolID().toString(), AppPrefs.getCurrentUser().first().userId, AppPrefs.getCurrentUser().first().role.rolename)
                settingsViewModel.saveNotificationState(true)
            } else {
                settingsViewModel.unregisterUserForPushNotification()
                settingsViewModel.saveNotificationState(false)
            }
        }

        autoSnapSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                settingsViewModel.saveKioskAutoSnapMode(true)
            } else {
                settingsViewModel.saveKioskAutoSnapMode(false)
            }
        }

        boundingBoxSwitch.setOnCheckedChangeListener { _, isChecked ->
            if (isChecked) {
                settingsViewModel.saveShowBoundingBoxState(true)
            } else {
                settingsViewModel.saveShowBoundingBoxState(false)
            }
        }

        distanceSeekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                updateDistanceTextView(progress)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {

            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                settingsViewModel.saveSnapDistance(seekBar.progress * 20000)
            }
        })

    }

    private fun updateUI() {
        //notification
        notificationSwitch.isChecked = AppPrefs.getNotificationState()

        //language
        val language = arrayOf("English")
        languageSpinner.adapter = ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, language)
        languageSpinner.setSelection(0)

        when(AppPrefs.getCurrentUser().first().role) {
            UserRole.ADMIN -> {

            }
            UserRole.TEACHER -> {

            }
            UserRole.PARENT -> {
                faceRecogTextView.visibility = View.GONE
                autoSnapLayout.visibility = View.GONE
                boundingBoxLayout.visibility = View.GONE
                distanceLayout.visibility = View.GONE

                autoSnapDivider.visibility = View.GONE
                boundingBoxDivider.visibility = View.GONE
                distanceDivider.visibility = View.GONE
            }
        }
        //auto snap
        autoSnapSwitch.isChecked = AppPrefs.getKioskAutoSnapMode()
        boundingBoxSwitch.isChecked = AppPrefs.getShowBoundingBoxState()

        distanceSeekBar.progress = AppPrefs.getSnapDistance() / 20000
        updateDistanceTextView(distanceSeekBar.progress)

    }

    private fun updateDistanceTextView(distance: Int) {
        distanceTextView.text = "Distance : ${distance.toString()}"
    }

    override fun onSupportNavigateUp(): Boolean {
        finish()
        return true
    }
}