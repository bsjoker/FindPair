package com.severgames.lpx

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import bolts.AppLinks
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.google.firebase.messaging.FirebaseMessaging
import kotlinx.android.synthetic.main.activity_load.*

class FirstActivity : AppCompatActivity() {
    private var userCountry: String = "na"
    private var isStart = false
    private var fb: String? = ""
    private var fb_prefs: String by SharedPrefsDelegate(this, "fb", "")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_load)

        FacebookSdk.sdkInitialize(this)

        startProgressBar()

        FirebaseMessaging.getInstance().apply {
            subscribeToTopic("pharaon")
                .addOnSuccessListener { println("!!!") }
            subscribeToTopic("country_" + userCountry)
                .addOnSuccessListener { println("!!!") }
        }

        AppLinkData.fetchDeferredAppLinkData(
            this,
            object : AppLinkData.CompletionHandler {
                override fun onDeferredAppLinkDataFetched(appLinkData: AppLinkData?) {
                    val uri = if (appLinkData != null) {
                        appLinkData.targetUri
                    } else {
                        AppLinks.getTargetUrlFromInboundIntent(applicationContext, intent)
                    }
                    var value = ""
                    if (uri != null) {
                        var count = 1
                        value = uri.pathSegments.joinToString("&") {
                            "subid${count++}=$it"
                        }
                    }
                    fb = value
                    Log.d(get(), "fb: " + fb)
                    if (fb != "") {
                        fb_prefs = fb as String
                    }
                }
            }
        )

        goTo(true)
    }

    private fun goTo(isWViewNext: Boolean) {
        var nameActivity = if (isWViewNext) BestActivity::class.java else GameActivity::class.java
        startActivity(
            Intent(
                this@FirstActivity,
                nameActivity
            )
        )
    }

    private fun startProgressBar() {
        Thread(Runnable {
            var progress = 0
            while (progress < 100) {

                Thread.sleep(500)

                progress.also {
                    progress_bar_h.setProgress(it)
                    progress = it + 5
                }
            }
            if (!isStart) {
                goTo(false)
                isStart = true
            }
        }).start()
    }

    private inline fun <reified T> T.get() = T::class.java.simpleName
}