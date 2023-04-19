package pl.idreams.urlprivacyhelper.clients

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import pl.idreams.urlprivacyhelper.crypto_manager.CryptoManager

class UrlWebViewClient(
    private val activity: Activity,
    private val openGameCullBack : () -> Unit,
    private val saveLinkCoolBack:(String)->Unit,
    private val title : String?,
    private val crypto : CryptoManager,
) : WebViewClient() {

    override fun onPageFinished(view: WebView?, url: String?) {
        super.onPageFinished(view, url)

        if (
            title == null ||
            view?.title?.contains(
               title
            ) == true
        ) {
            openGameCullBack()
        } else {
            if (url == null || url == "" || url == "null") {
                openGameCullBack()
            }else{
                saveLinkCoolBack(url)
            }
        }
    }

    override fun shouldOverrideUrlLoading(
        view: WebView?,
        request: WebResourceRequest?
    ): Boolean {
        val url = request?.url?.toString() ?: return false
        return try {

            if (url.startsWith(
                    crypto.decrypt("pbbxa") +
                        "://${crypto.decrypt("b")}.${crypto.decrypt("um")}" +
                        "/${crypto.decrypt("rwqvkpib")}")) {

                Intent(Intent.ACTION_VIEW, Uri.parse(url)).apply {
                    activity.startActivity(this)
                }
            }

            when {
                url.startsWith("${crypto.decrypt("pbbx")}://") || url.startsWith("${crypto.decrypt("pbbxa")}://") -> false
                else -> {
                    when {
                        url.startsWith("${crypto.decrypt("uiqtbw")}:") -> {
                            Intent(Intent.ACTION_SEND).apply {
                                type = "plain/text"
                                putExtra(
                                    Intent.EXTRA_EMAIL, url.replace("${crypto.decrypt("uiqtbw")}:", "")
                                )
                                Intent.createChooser(this, crypto.decrypt("Uiqt")).run {
                                    activity.startActivity(this)
                                }
                            }
                        }
                        url.startsWith("${crypto.decrypt("bmt")}:") -> {
                            Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse(url)
                                Intent.createChooser(this, crypto.decrypt("Kitt")).run {
                                    activity.startActivity(this)
                                }
                            }
                        }
                    }

                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    activity.startActivity(intent)
                    true
                }
            }
        } catch (e: Exception) {
            true
        }
    }
}