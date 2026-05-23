package icu.nullptr.applistdetector

import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager

import java.util.zip.ZipFile

class XposedModules(
    context: Context,
    override val name: String,
    private val lspatch: Boolean
) : IDetector(context) {

    @SuppressLint("QueryPermissions OR PMCAPermissions Needed")
    override fun run(packages: Collection<String>?, detail: Detail?): Result {
        if (packages != null) throw IllegalArgumentException("packages should be null")

        var result = Result.NOT_FOUND
        val pm = context.packageManager
        val set = if (detail == null) null else mutableSetOf<Pair<String, Result>>()

        val apps = pm.getInstalledApplications(PackageManager.GET_META_DATA)

        val meta: String
        val meta2: String
        if (lspatch) {
            meta = "lspatch"
            meta2 = "jshook"
        } else {
            meta = "xposedminversion"
            meta2 = "xposeddescription"
        }

        for (pkg in apps) {
            val label = pm.getApplicationLabel(pkg) as String
            var found = false
            if (pkg.metaData?.get(meta) != null || pkg.metaData?.get(meta2) != null) {
                found = true
            }
            val apkPath = pkg.sourceDir
            try {
                ZipFile(apkPath).use { zip ->
                    if (zip.getEntry("META-INF/xposed/") != null ||
                        zip.getEntry("META-INF/xposed/module.prop") != null
                    ) {
                        found = true
                    }
                }
            } catch (e: Exception) {
            }

            if (found) {
                result = Result.FOUND
                set?.add(label to Result.FOUND)
            }
        }

        detail?.addAll(set ?: emptySet())
        return result
    }
}
