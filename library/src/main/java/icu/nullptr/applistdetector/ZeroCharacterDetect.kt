package icu.nullptr.applistdetector

import android.content.Context
import android.util.Log
import java.io.File

class ZeroCharacterDetect(context: Context, override val name: String) : IDetector(context) {
    override fun run(
        packages: Collection<String>?,
        detail: Detail?
    ): Result {
        if (packages == null) throw IllegalArgumentException("packages should not be null")
        val androidDataPath = "/storage/emulated/0/Android/‍data"
        val dirsList: Array<out String?>? = File(androidDataPath).list()
        var result = Result.NOT_FOUND
        if (dirsList != null) {
            for (pkg in packages) {
                val res =
                    if (dirsList.contains(pkg)) Result.FOUND
                    else Result.NOT_FOUND
                result = result.coerceAtLeast(res)
                detail?.add(pkg to res)
            }
        } else {
            //Android主线尚未修复这个漏洞 拿不到就是有问题
            return Result.SUSPICIOUS
        }
        return result
    }
}