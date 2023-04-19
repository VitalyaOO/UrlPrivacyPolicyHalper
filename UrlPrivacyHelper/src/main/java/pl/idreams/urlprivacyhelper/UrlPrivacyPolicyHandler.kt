package pl.idreams.urlprivacyhelper

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.webkit.WebView
import androidx.activity.OnBackPressedDispatcher
import androidx.lifecycle.LifecycleOwner
import pl.idreams.urlprivacyhelper.clients.UrlWebChromeClient
import pl.idreams.urlprivacyhelper.clients.UrlWebViewClient
import pl.idreams.urlprivacyhelper.model.*

interface UrlPrivacyPolicyHandler {

    var myUri: Uri?

    var isVersionLvlHigh : Boolean

    fun initOneSignal(oneSignalId: String, context: Context)

    fun initReferrer(activity: Activity, responseCallback: (String?) -> Unit)

    fun getRefData(referrer: String?, fbDescriptionKey: String): ReferrerData?

    fun generateLink(
        tracker: String,
        parseToolsData: ParseToolsData,
        valuesData: ValuesData?,
        inputData: InputData,
    )
            : String

    fun sendOneSignal(push: String?, appsId: String?)

    fun getWebChromeClient(
        activity: Activity,
        openPhotoRequestCode: Int,
    ): UrlWebChromeClient

    fun getWebViewClient(
        activity: Activity,
        title: String,
        errorCullBack: () -> Unit,
        saveDataCallBack : (String) -> Unit
    ): UrlWebViewClient

    fun initAppsFlyer(
        activity: Activity,
        afKey: String,
        cullBack: (AppsData?) -> Unit
    )

    suspend fun getDeepLink(
        context: Context,
        fbId: String,
        fbToken: String,
        cullBack: (String?) -> Unit
    )

    fun parseValues(
        referrerData: ReferrerData?,
        appsData: AppsData?,
        deepLink: String?,
    ): ParseToolsData

    suspend fun getSystemValues(context: Context): ValuesData

    fun setBasicWebViewSettings(webView: WebView)

    fun handleOnBackPressed(
        lifecycleOwner: LifecycleOwner,
        webView: WebView,
        mainUrl: String,
        onBackPressedDispatcher: OnBackPressedDispatcher
    )

    fun createFile(activity: Activity, bmp: Bitmap, isVersionLvlHigh: Boolean): Uri?

    fun handlePhotoOnActivityResult(
        activity: Activity,resultCode: Int, data : Intent?, urlWebChromeClient: UrlWebChromeClient
    )
}