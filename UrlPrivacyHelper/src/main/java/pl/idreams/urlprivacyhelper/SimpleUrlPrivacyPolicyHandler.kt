package pl.idreams.urlprivacyhelper

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.BatteryManager
import android.os.Build
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.webkit.CookieManager
import android.webkit.WebSettings
import android.webkit.WebView
import androidx.activity.OnBackPressedCallback
import androidx.activity.OnBackPressedDispatcher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import com.android.installreferrer.api.InstallReferrerClient
import com.android.installreferrer.api.InstallReferrerStateListener
import com.appsflyer.AppsFlyerConversionListener
import com.appsflyer.AppsFlyerLib
import com.facebook.FacebookSdk
import com.facebook.applinks.AppLinkData
import com.google.android.gms.ads.identifier.AdvertisingIdClient
import com.onesignal.OneSignal
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import pl.idreams.urlprivacyhelper.clients.UrlWebChromeClient
import pl.idreams.urlprivacyhelper.clients.UrlWebViewClient
import pl.idreams.urlprivacyhelper.crypto_manager.CryptoManagerImpl
import pl.idreams.urlprivacyhelper.model.*
import java.io.IOException
import java.net.URLDecoder
import java.net.URLEncoder
import java.text.SimpleDateFormat
import java.util.*
import javax.crypto.Cipher
import javax.crypto.spec.IvParameterSpec
import javax.crypto.spec.SecretKeySpec

class SimpleUrlPrivacyPolicyHandler : UrlPrivacyPolicyHandler {

    override var myUri: Uri? = null
    override var isVersionLvlHigh: Boolean = false

    private val crypto = CryptoManagerImpl()

    override fun initOneSignal(oneSignalId: String, context: Context) {
        OneSignal.initWithContext(context)
        OneSignal.setAppId(oneSignalId)
        OneSignal.setLogLevel(
            OneSignal.LOG_LEVEL.VERBOSE,
            OneSignal.LOG_LEVEL.NONE
        )
    }

    override fun getRefData(referrer: String?, fbDescriptionKey: String): ReferrerData? {
        referrer ?: return null

        return try {
            val referrerParams = referrer.split("_${crypto.decrypt("kwvbmvb")}=")
                .getOrNull(1)
                ?.let { URLDecoder.decode(it, "UTF-8") }
                ?.let { JSONObject(it) }
                ?.getJSONObject(crypto.decrypt("awczkm"))
                ?: return null

            val data = referrerParams.optString(crypto.decrypt("libi"))
            val nonce = referrerParams.optString(crypto.decrypt("vwvkm"))

            val decodedData = data.decodeHex()
            val decodedNonce = nonce.decodeHex()
            val fbKey = fbDescriptionKey.decodeHex()

            val cipher = Cipher.getInstance("AES/GCM/NoPadding")
            val spec = SecretKeySpec(fbKey, "AES/GCM/NoPadding")
            val nonceSpec = IvParameterSpec(decodedNonce)
            cipher.init(Cipher.DECRYPT_MODE, spec, nonceSpec)

            val decryptedData = cipher.doFinal(decodedData)
            val decryptedDataJson = JSONObject(String(decryptedData))

            ReferrerData(
                aI = decryptedDataJson.optString(crypto.decrypt("il") +
                        "_${crypto.decrypt("ql")}"),
                aN = decryptedDataJson.optString(crypto.decrypt("ilozwcx") +
                        "_${crypto.decrypt("vium")}"),
                cI = decryptedDataJson.optString(crypto.decrypt("kiuxiqov") +
                        "_${crypto.decrypt("ql")}"),
                cGN = decryptedDataJson.optString(crypto.decrypt("kiuxiqov") +
                        "_${crypto.decrypt("ozwcx")}_${crypto.decrypt("vium")
                    }"
                ),
                acI = decryptedDataJson.optString(crypto.decrypt("ikkwcvb") +
                        "_${crypto.decrypt("ql")}"),
                iI = decryptedDataJson.optString(
                    crypto.decrypt("qa") + "_${crypto.decrypt("qvabioziu")}")
            )
        } catch (e: Exception) {
            Log.e("getReferrerData", e.message.toString())
            null
        }
    }

    private fun String.decodeHex(): ByteArray {
        check(length % 2 == 0) { "Length exception" }
        return chunked(2).map { it.toInt(16).toByte() }.toByteArray()
    }


    override fun initReferrer(activity: Activity, responseCallback: (String?) -> Unit) {
        try {
            val referrerClient = InstallReferrerClient.newBuilder(activity).build()
            referrerClient.startConnection(object : InstallReferrerStateListener {
                override fun onInstallReferrerSetupFinished(responseCode: Int) {
                    when (responseCode) {
                        InstallReferrerClient.InstallReferrerResponse.OK -> {
                            val installReferrer = referrerClient.installReferrer.installReferrer
                            responseCallback(installReferrer)
                        }
                        else -> {
                            responseCallback(null)
                        }
                    }
                    referrerClient.endConnection()
                }

                override fun onInstallReferrerServiceDisconnected() {
                    responseCallback(null)
                }
            })
        } catch (e: Exception) {
            Log.e("initReferrer", e.message.toString())
            responseCallback(null)
        }
    }

    override fun generateLink(
        tracker: String,
        parseToolsData: ParseToolsData,
        valuesData: ValuesData?,
        inputData: InputData,
    ): String {
        val linkBuilder = StringBuilder()

        val aC = when (parseToolsData.aC) {
            crypto.decrypt("bzcm") -> crypto.decrypt("Qvabioziu")
            crypto.decrypt("nitam") -> crypto.decrypt("Nikmjwws")
            else -> parseToolsData.aC
        }

        val mS = parseToolsData.mS
            ?: when (parseToolsData.aC) {
                crypto.decrypt("bzcm") -> crypto.decrypt("Qvabioziu")
                crypto.decrypt("nitam") -> "${crypto.decrypt("Nikmjwws")} ${crypto.decrypt("Ila")}"
                else -> parseToolsData.aC
            }

        linkBuilder.apply {
            append(tracker)
            append("${crypto.decrypt("acj")}1=${parseToolsData.sL?.getOrNull(0) ?: "null"}&")
            for (i in 2..10) {
                append("${crypto.decrypt("acj")}$i=${parseToolsData.sL?.getOrNull(i) ?: ""}&")
            }
            append("${crypto.decrypt("kiuxiqov")}=${parseToolsData.camp}&")
            append("${crypto.decrypt("ikkwcvb")}_${crypto.decrypt("ql")}=${parseToolsData.acI}&")
            append("${crypto.decrypt("owwotm")}_${crypto.decrypt("ilql")}=${valuesData?.gId}&")
            append("${crypto.decrypt("in")}_${crypto.decrypt("camzql")}=${valuesData?.appsFlyerId}&")
            append("${crypto.decrypt("umlqi")}_${crypto.decrypt("awczkm")}=$mS&")
            append("${crypto.decrypt("in")}_${crypto.decrypt("kpivvmt")}=$aC&")
            append("${crypto.decrypt("in")}_${crypto.decrypt("abibca")}=${parseToolsData.afS}&")
            append("${crypto.decrypt("ilj")}=${valuesData?.isDevelopmentSettingEnabled}&")
            append("${crypto.decrypt("jibbmzg")}=${valuesData?.batteryLvl}&")
            append("${crypto.decrypt("in")}_${crypto.decrypt("il")}=${parseToolsData.aA}&")
            append("${crypto.decrypt("kiuxiqov")}_${crypto.decrypt("ql")}=${parseToolsData.cI}&")
            append("${crypto.decrypt("ilamb")}_${crypto.decrypt("ql")}=${parseToolsData.aSI}&")
            append("${crypto.decrypt("il")}_${crypto.decrypt("ql")}=${parseToolsData.aI}&")
            append("${crypto.decrypt("ilamb")}=${parseToolsData.adS}&")
            append("${crypto.decrypt("jcvltm")}=${inputData.bundle}&")
            append("${crypto.decrypt("xcap")}=${parseToolsData.sL?.getOrNull(1) ?: "null"}&")
            append("${crypto.decrypt("lmd")}_${crypto.decrypt("smg")}=${inputData.AppsFlayerKey}&")
            append("${crypto.decrypt("nj")}_${crypto.decrypt("ixx")}_${crypto.decrypt("ql")}=${inputData.FBID}&")
            append("${crypto.decrypt("nj")}_${crypto.decrypt("ib")}=${inputData.FBToken}&")
        }
        return linkBuilder.toString()
    }

    override fun sendOneSignal(push: String?, appsId: String?) {
        OneSignal.setExternalUserId(appsId ?: "")
        OneSignal.sendTag(
            "${crypto.decrypt("acj")}_${crypto.decrypt("ixx")}",
            (push ?: crypto.decrypt("wzoivqk"))
        )
    }

    override fun getWebChromeClient(
        activity: Activity,
        openPhotoRequestCode: Int,
    ): UrlWebChromeClient {
        return UrlWebChromeClient(activity, openPhotoRequestCode)
    }

    override fun getWebViewClient(
        activity: Activity,
        title: String,
        errorCullBack: () -> Unit,
        saveDataCallBack : (String) -> Unit
    ): UrlWebViewClient {
        return UrlWebViewClient(
            activity,
            errorCullBack,
            saveLinkCoolBack = {saveDataCallBack(it)},
            title = title,
            crypto = crypto
        )
    }

    override fun initAppsFlyer(
        activity: Activity,
        afKey: String,
        cullBack: (AppsData?) -> Unit
    ) {
        val conversionListener = object : AppsFlyerConversionListener {
            override fun onConversionDataSuccess(data: MutableMap<String, Any>?) {
                val appsData = AppsData(
                    aS = data?.getOrDefault(
                        crypto.decrypt("in") +
                                "_${crypto.decrypt("abibca")}", null
                    ) as String?,
                    aC = data?.getOrDefault(
                        crypto.decrypt("in") +
                                "_${crypto.decrypt("kpivvmt")}", null
                    ) as String?,
                    camp = data?.getOrDefault(crypto.decrypt("kiuxiqov"), null)
                            as String?,
                    mS = data?.getOrDefault(
                        crypto.decrypt("umlqi") +
                                "_${crypto.decrypt("awczkm")}", null
                    ) as String?,
                    aA = data?.getOrDefault(
                        crypto.decrypt("in") +
                                "_${crypto.decrypt("il")}", null
                    ) as String?,
                    cI = data?.getOrDefault(
                        crypto.decrypt("kiuxiqov") +
                                "_${crypto.decrypt("ql")}", null
                    ) as String?,
                    aSI = data?.getOrDefault(
                        crypto.decrypt("ilamb") +
                                "_${crypto.decrypt("ql")}", null
                    ) as String?,
                    aI = data?.getOrDefault(
                        crypto.decrypt("il") +
                                "_${crypto.decrypt("ql")}", null
                    ) as String?,
                    adS = data?.getOrDefault(crypto.decrypt("ilamb"), null)
                            as String?
                )
                cullBack(appsData)
            }

            override fun onConversionDataFail(error: String?) {
                cullBack(null)
            }

            override fun onAppOpenAttribution(data: MutableMap<String, String>?) {
                cullBack(null)
            }

            override fun onAttributionFailure(error: String?) {
                cullBack(null)
            }
        }

        CoroutineScope(Dispatchers.IO).launch {
            AppsFlyerLib.getInstance().apply {
                init(afKey, conversionListener, activity)
                start(activity)
            }
        }
    }

    @Suppress("DEPRECATION")
    override suspend fun getDeepLink(
        context: Context,
        fbId: String,
        fbToken: String,
        cullBack: (String?) -> Unit
    ) {
        withContext(Dispatchers.IO) {

            FacebookSdk.apply {
                setApplicationId(fbId)
                setClientToken(fbToken)
                sdkInitialize(context)
                setAdvertiserIDCollectionEnabled(true)
                setAutoInitEnabled(true)
                fullyInitialize()
            }

            AppLinkData.fetchDeferredAppLinkData(context) {
                cullBack(it?.targetUri?.toString())
            }
        }
    }

    override fun parseValues(
        referrerData: ReferrerData?,
        appsData: AppsData?,
        deepLink: String?
    ): ParseToolsData {
        val campaign = appsData?.camp ?: referrerData?.cGN
        var resultCampaign = campaign
        var subList: List<String>? = null

        when {
            deepLink != null && deepLink.isNotBlank() -> {
                try {
                    resultCampaign = deepLink.split("://").getOrNull(1)
                    subList = resultCampaign?.split("_")
                } catch (e: Exception) {
                    Log.e("parseValues", e.message.toString())
                    e.printStackTrace()
                }
            }
            resultCampaign != null && resultCampaign != "null" -> {
                try {
                    subList = resultCampaign.split("_")
                } catch (e: Exception) {
                    Log.e("parseValues", e.message.toString())
                    e.printStackTrace()
                }
            }
        }

        val referrer = referrerData?.let {
            ParseToolsData(
                mS = null,
                aC = encodeUrl(it.iI.toString()),
                adS = encodeUrl(it.aN.toString()),
                aSI = null,
                cI = encodeUrl(it.cI.toString()),
                aA = null,
                aI = encodeUrl(it.aI.toString()),
                afS = null,
                camp = encodeUrl(resultCampaign.toString()),
                acI = encodeUrl(it.acI.toString()),
                sL = subList
            )
        }

        val apps = appsData?.let { apps ->
            ParseToolsData(
                mS = encodeUrl(apps.mS.toString()),
                aC = encodeUrl(apps.aC.toString()),
                adS = encodeUrl(apps.adS.toString()),
                aSI = encodeUrl(apps.aSI.toString()),
                cI = encodeUrl(apps.cI.toString()),
                aA = encodeUrl(apps.aA.toString()),
                aI = encodeUrl(apps.aI.toString()),
                afS = encodeUrl(apps.aS.toString()),
                camp = resultCampaign,
                acI = referrerData?.let { encodeUrl(it.acI.toString()) },
                sL = subList
            )
        }

        return referrer ?: apps ?: ParseToolsData(
            null, null, null, null,
            null, null, null, null, null, null, null
        )
    }

    override suspend fun getSystemValues(context: Context): ValuesData =
        withContext(Dispatchers.IO) {
            val batteryLevel = try {
                val batteryManager =
                    context.getSystemService(Context.BATTERY_SERVICE) as BatteryManager
                batteryManager.getIntProperty(BatteryManager.BATTERY_PROPERTY_CAPACITY)
            } catch (e: Exception) {
                Log.e("getSystemValues", e.message.toString())
                100
            }

            val appsFlyerId = AppsFlyerLib.getInstance().getAppsFlyerUID(context)

            val advertisingInfo = runCatching {
                AdvertisingIdClient.getAdvertisingIdInfo(context)
            }.getOrNull()

            val gId = advertisingInfo?.id

            val isDevelopmentSettingEnabled = Settings.Global.getInt(
                context.contentResolver,
                Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
                0
            ) != 0

            ValuesData(
                batteryLvl = batteryLevel.toFloat().toString(),
                appsFlyerId = appsFlyerId,
                gId = gId,
                isDevelopmentSettingEnabled = isDevelopmentSettingEnabled
            )
        }

    @SuppressLint("SetJavaScriptEnabled")
    override fun setBasicWebViewSettings(webView: WebView) {
       /* webView.apply {
            settings.apply {
                userAgentString = userAgentString.replace("; wv", "")
                mixedContentMode = WebSettings.MIXED_CONTENT_COMPATIBILITY_MODE
                javaScriptEnabled = true
                domStorageEnabled = true
                loadsImagesAutomatically = true
                databaseEnabled = true
                useWideViewPort = true
                allowFileAccess = true
                javaScriptCanOpenWindowsAutomatically = true
                loadWithOverviewMode = true
                allowContentAccess = true
                setSupportMultipleWindows(false)
                builtInZoomControls = true
                displayZoomControls = false
                cacheMode = WebSettings.LOAD_DEFAULT

                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) {
                    @Suppress("DEPRECATION")
                    saveFormData = true
                }
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                importantForAutofill = View.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
            }

            CookieManager.getInstance().apply {
                setAcceptCookie(true)
                setAcceptThirdPartyCookies(webView, true)
            }

            isSaveEnabled = true
            isFocusable = true
            isFocusableInTouchMode = true
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            setLayerType(View.LAYER_TYPE_HARDWARE, null)
        }*/

        webView.apply {
            CookieManager.getInstance().setAcceptCookie(true)
            CookieManager.getInstance().setAcceptThirdPartyCookies(this, true)
            isSaveEnabled = true
            isFocusable = true
            isFocusableInTouchMode = true
            isVerticalScrollBarEnabled = false
            isHorizontalScrollBarEnabled = false
            setInitialScale(200)         //change useWideViewPort = true
            setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                importantForAutofill = WebView.IMPORTANT_FOR_AUTOFILL_NO_EXCLUDE_DESCENDANTS
            }
        }
        webView.settings.apply {
            mixedContentMode = 0
            javaScriptEnabled = true
            domStorageEnabled = true
            loadsImagesAutomatically = true
            databaseEnabled = true
            allowFileAccess = true
            javaScriptCanOpenWindowsAutomatically = true
            loadWithOverviewMode = true
            allowContentAccess = true
            setSupportMultipleWindows(false)
            builtInZoomControls = true
            displayZoomControls = false
            cacheMode = WebSettings.LOAD_DEFAULT
            userAgentString = userAgentString.replace("; wv","")
            @Suppress("DEPRECATION")
            if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.O_MR1) saveFormData = true
        }
    }

    override fun handleOnBackPressed(
        lifecycleOwner: LifecycleOwner,
        webView: WebView,
        mainUrl: String,
        onBackPressedDispatcher: OnBackPressedDispatcher
    ) {
        var doublePress = false

        onBackPressedDispatcher.addCallback(
            lifecycleOwner,
            object : OnBackPressedCallback(true) {
                override fun handleOnBackPressed() {
                    webView.apply {
                        if (canGoBack()) {
                            if (doublePress) {
                                loadUrl(mainUrl)
                            }

                            doublePress = true
                            goBack()
                            Handler(Looper.getMainLooper()).postDelayed({
                                doublePress = false
                            }, 1990)
                        }
                    }
                }
            }
        )
    }

    @SuppressLint("InlinedApi")
    override fun createFile(activity: Activity, bmp: Bitmap, isVersionLvlHigh: Boolean): Uri? {
        val imageCollection = if (isVersionLvlHigh) {
            MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY)
        } else {
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        }

        val timeStamp = SimpleDateFormat.getDateInstance().format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"

        val contentValues = ContentValues().apply {
            put(MediaStore.Images.Media.DISPLAY_NAME, imageFileName)
            put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
            put(MediaStore.Images.Media.WIDTH, bmp.width)
            put(MediaStore.Images.Media.HEIGHT, bmp.height)
        }

        return try {
            activity.contentResolver.insert(imageCollection, contentValues)?.also { uri ->
                activity.contentResolver.openOutputStream(uri)?.use { outputStream ->
                    if (!bmp.compress(Bitmap.CompressFormat.JPEG, 95, outputStream)) {
                        throw IOException("Photo saving error")
                    }
                }
            } ?: throw IOException("Creating path error")
        } catch (e: IOException) {
            e.printStackTrace()
            Log.e("createFile", e.message.toString())
            null
        }
    }

    @Suppress("DEPRECATION")
    override fun handlePhotoOnActivityResult(
        activity: Activity,
        resultCode: Int,
        data: Intent?,
        urlWebChromeClient: UrlWebChromeClient
    ) {
        when {
            resultCode != AppCompatActivity.RESULT_OK -> {
                urlWebChromeClient.mFilePathCallback?.onReceiveValue(null)
            }
            data == null -> {
                urlWebChromeClient.mFilePathCallback?.onReceiveValue(null)
            }
            data.data != null -> {
                val selectedImageUri = data.data
                urlWebChromeClient.mFilePathCallback?.onReceiveValue(
                    arrayOf(selectedImageUri)
                        .filterNotNull().toTypedArray()
                )
            }
            else -> {
                val bitmap = data.extras?.get("data") as? Bitmap
                CoroutineScope(Dispatchers.Default).launch {
                    val uri = bitmap?.let {
                        withContext(Dispatchers.IO) {
                            createFile(
                                activity,
                                it,
                                isVersionLvlHigh
                            )
                        }
                    }
                    urlWebChromeClient.mFilePathCallback?.onReceiveValue(
                        arrayOf(uri)
                            .filterNotNull().toTypedArray()
                    )
                }
            }
        }
    }

    private fun encodeUrl(url: String): String {
        return try {
            URLEncoder.encode(url, "utf-8")
        } catch (e: Exception) {
            Log.e("encodeUrl", e.message.toString())
            "null"
        }
    }
}
