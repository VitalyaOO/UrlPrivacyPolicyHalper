package pl.idreams.urlprivacyhelper.crypto_manager

class CryptoManagerImpl : CryptoManager {
    override fun decrypt(string : String): String {
        val offset = 8
        val sb = StringBuilder()
        for (element in string) {
            if (element in 'A'..'Z') {
                var t1 = element.code - 'A'.code - offset
                if (t1 < 0) t1 += 26
                sb.append((t1 + 'A'.code).toChar())
            } else if (element in 'a'..'z') {
                var t1 = element.code - 'a'.code - offset
                if (t1 < 0) t1 += 26
                sb.append((t1 + 'a'.code).toChar())
            }
        }
        return sb.toString()
    }
}