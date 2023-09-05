package i.library.base.utils

import java.security.MessageDigest

/**
 * Created by hc. on 2021/5/27
 * Describe:
 */
object ThisMD5Utils {
    private val HEX_CHARS = "0123456789abcdef".toCharArray()

    fun toGetPassword(account : String,password : String) : String{
        return md5(account + password + "Boby")
    }

    private fun md5(input: String): String {
        val bytes = MessageDigest.getInstance("MD5").digest(input.toByteArray())
        return printHexBinary(bytes)
    }

    private fun printHexBinary(data: ByteArray): String {
        val r = StringBuilder(data.size * 2)
        data.forEach { b ->
            val i = b.toInt()
            r.append(HEX_CHARS[i shr 4 and 0xF])
            r.append(HEX_CHARS[i and 0xF])
        }
        return r.toString()
    }
}