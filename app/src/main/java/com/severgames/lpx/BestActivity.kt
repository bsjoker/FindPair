package com.severgames.lpx

import android.annotation.TargetApi
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.KeyEvent
import android.view.View
import android.webkit.*
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.common.util.Strings
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_webview.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class BestActivity : AppCompatActivity() {
    companion object {
        const val TAG = "WebViewActivity"
    }

    var count = 0
    val INPUT_FILE_REQUEST_CODE = 1
    private var filePathCallback1: ValueCallback<Array<Uri>>? = null
    private var photoPath: String? = null
    private lateinit var link: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview)

        var subs: String by SharedPrefsDelegate(this, "fb", "")

        FirebaseDatabase.getInstance().getReference().child("url").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val value: String? = dataSnapshot.getValue(String::class.java)
                link = "$value?$subs"
                webView.loadUrl(link)
                Log.d(TAG, "Subs: $link")
            }

            override fun onCancelled(databaseError: DatabaseError) {
                Log.w(TAG, "Failed to read value.", databaseError.toException())
            }
        })

        CookieManager.getInstance().setAcceptCookie(true)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            with(CookieManager.getInstance()) {
                setAcceptThirdPartyCookies(webView, true)
            }
        }

        val webSet = webView.getSettings()

        with(webSet) {
            setJavaScriptEnabled(true)
            setDomStorageEnabled(true)
            setLoadWithOverviewMode(true)
            setUseWideViewPort(true)
            setBuiltInZoomControls(true)
            setDisplayZoomControls(false)
            setSupportZoom(true)
            setDefaultTextEncodingName("utf-8")
            setAllowFileAccess(true)
            setSupportMultipleWindows(true)
        }

        with(webView) {
            getSettings().setPluginState(WebSettings.PluginState.ON)
            setWebViewClient(MyWebViewClient())
            setWebChromeClient(
                object : WebChromeClient() {

                    override fun onConsoleMessage(consoleMessage: ConsoleMessage?): Boolean {
                        consoleMessage?.let {
                            if (it.message() == "user_accepted_action") {
                                startActivity(Intent(this@BestActivity, StartActivity::class.java))
                            }
                        }
                        return super.onConsoleMessage(consoleMessage)
                    }

                    override fun onShowFileChooser(
                        webView: WebView, filePathCallback: ValueCallback<Array<Uri>>,
                        fileChooserParams: WebChromeClient.FileChooserParams
                    ): Boolean {
                        if (filePathCallback1 != null) {
                            filePathCallback1!!.onReceiveValue(null)
                        }
                        filePathCallback1 = filePathCallback

                        var takePictureIntent: Intent? = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                        if (takePictureIntent!!.resolveActivity(getPackageManager()) != null) {
                            // Create the File where the photo should go
                            var photoFile: File? = null
                            try {
                                photoFile = createImageFile()
                                takePictureIntent.putExtra("PhotoPath", photoPath)
                            } catch (ex: IOException) {
                                // Error occurred while creating the File
                                Log.e(TAG, "Unable to create Image File", ex)
                            }

                            // Continue only if the File was successfully created
                            if (photoFile != null) {
                                photoPath = "file:" + photoFile.absolutePath
                                takePictureIntent.putExtra(
                                    MediaStore.EXTRA_OUTPUT,
                                    Uri.fromFile(photoFile)
                                )
                            } else {
                                takePictureIntent = null
                            }
                        }

                        val contentSelectionIntent = Intent(Intent.ACTION_GET_CONTENT)
                        with(contentSelectionIntent) {
                            addCategory(Intent.CATEGORY_OPENABLE)
                            type = "image/*"
                        }
                        val intentArray: Array<Intent?>
                        if (takePictureIntent != null) {
                            intentArray = arrayOf(takePictureIntent)
                        } else {
                            intentArray = arrayOfNulls(0)
                        }

                        val chooserIntent = Intent(Intent.ACTION_CHOOSER)
                        with(chooserIntent) {
                            putExtra(Intent.EXTRA_INTENT, contentSelectionIntent)
                            putExtra(Intent.EXTRA_TITLE, "Image Chooser")
                            putExtra(Intent.EXTRA_INITIAL_INTENTS, intentArray)
                        }
                        startActivityForResult(chooserIntent, INPUT_FILE_REQUEST_CODE)

                        return true
                    }
                })

            var url: String by SharedPrefsDelegate(this@BestActivity, "url", "")
            loadUrl(url)
        }
    }

    /**
     * More info this method can be found at
     * http://developer.android.com/training/camera/photobasics.html
     *
     * @return
     * @throws IOException
     */
    @Throws(IOException::class)
    private fun createImageFile(): File {
        // Create an image file name
        val timeStamp = SimpleDateFormat("yyyyMMdd_HHmmss").format(Date())
        val imageFileName = "JPEG_" + timeStamp + "_"
        val storageDir = Environment.getExternalStoragePublicDirectory(
            Environment.DIRECTORY_PICTURES
        )
        return File.createTempFile(
            imageFileName, /* prefix */
            ".jpg", /* suffix */
            storageDir      /* directory */
        )
    }

    /**
     * Convenience method to set some generic defaults for a
     * given WebView
     *
     * @param webView
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    private fun setUpWebViewDefaults(webView: WebView) {
        val settings = webView.settings

        // Enable Javascript
        settings.javaScriptEnabled = true

        // Use WideViewport and Zoom out if there is no viewport defined
        settings.useWideViewPort = true
        settings.loadWithOverviewMode = true

        // Enable pinch to zoom without the zoom buttons
        settings.builtInZoomControls = true

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.HONEYCOMB) {
            // Hide the zoom controls for HONEYCOMB+
            settings.displayZoomControls = false
        }

        // Enable remote debugging via chrome://inspect
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            WebView.setWebContentsDebuggingEnabled(true)
        }

        // We set the WebViewClient to ensure links are consumed by the WebView rather
        // than passed to a browser if it can
        this.webView.setWebViewClient(WebViewClient())
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode != INPUT_FILE_REQUEST_CODE || filePathCallback1 == null) {
            super.onActivityResult(requestCode, resultCode, data)
            return
        }

        var results: Array<Uri>? = null

        // Check that the response is a good one
        if (resultCode == Activity.RESULT_OK) {
            if (data == null) {
                // If there is not data, then we may have taken a photo
                if (photoPath != null) {
                    results = arrayOf(Uri.parse(photoPath))
                }
            } else {
                val dataString = data.dataString
                if (dataString != null) {
                    results = arrayOf(Uri.parse(dataString))
                }
            }
        }

        filePathCallback1!!.onReceiveValue(results)
        filePathCallback1 = null
        return
    }

    private inner class MyWebViewClient : WebViewClient() {
//        override fun shouldOverrideUrlLoading(view: WebView, url: String): Boolean {
//            view.loadUrl(url)
//            return true
//        }

        override fun onPageFinished(view: WebView?, url: String?) {
            super.onPageFinished(view, url)
            progressCircle.visibility = View.GONE
            url?.let {
                if (it == "about:blank") {
                    count++
                }
            }
            if (count > 1) {
                finishAffinity()
            }
        }
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent): Boolean {
        if (event.getAction() === KeyEvent.ACTION_DOWN) {
            when (keyCode) {
                KeyEvent.KEYCODE_BACK -> {
                    if (webView.canGoBack()) {
                        webView.goBack()
                    } else {
                        finishAffinity()
                    }
                    return true
                }
            }
        }
        return super.onKeyDown(keyCode, event)
    }
}
