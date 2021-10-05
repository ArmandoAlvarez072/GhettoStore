package com.example.ghetto.settings

import android.os.Bundle
import android.widget.Toast
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.example.ghetto.R
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.ktx.messaging

class SettingsFragment : PreferenceFragmentCompat() {

    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)

        val switchPreferenceCompat = findPreference<SwitchPreferenceCompat>(getString(R.string.pref_offers_key))
        switchPreferenceCompat?.setOnPreferenceChangeListener { preference, newValue ->
            (newValue as? Boolean)?.let{ isChecked ->
                val topic = getString(R.string.settings_topic_offer)
                if (isChecked) {
                    Firebase.messaging.subscribeToTopic(topic)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Notificaciones Activadas", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    Firebase.messaging.unsubscribeFromTopic(topic)
                        .addOnSuccessListener {
                            Toast.makeText(context, "Notificaciones Desactivadas", Toast.LENGTH_SHORT).show()

                        }
                }
            }

            true
        }
    }
}