package icu.nullptr.applistdetector

import android.content.Context
import android.content.SharedPreferences
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.byxiaorun.detector.MyApplication.Companion.accList
import com.byxiaorun.detector.MyApplication.Companion.accenable
import com.byxiaorun.detector.MyApplication.Companion.accountList
import com.byxiaorun.detector.MyApplication.Companion.adbenable
import com.byxiaorun.detector.MyApplication.Companion.appContext
import com.byxiaorun.detector.MyApplication.Companion.development_enable
import com.byxiaorun.detector.MyApplication.Companion.vpn_connect
import com.byxiaorun.detector.gettext
import icu.nullptr.applistdetector.component.CheckCard
import icu.nullptr.applistdetector.component.IconHintCard
import kotlinx.coroutines.*

val basicAppList = mutableListOf(
    "com.topjohnwu.magisk",
    "io.github.vvb2060.magisk",
    "io.github.vvb2060.magisk.lite",
    "de.robv.android.xposed.installer",
    "org.meowcat.edxposed.manager",
    "org.lsposed.manager",
    "top.canyie.dreamland.manager",
    "me.weishu.exp",
    "com.tsng.hidemyapplist",
    "cn.geektang.privacyspace",
    "moe.shizuku.redirectstorage",
    //应该就这些
    "me.weishu.kernelsu",
    "me.bmax.apatch",
    "io.github.huskydg.magisk"
)

val snapShotList = mutableStateListOf<Triple<IDetector, IDetector.Result?, Detail?>>(
    Triple(AbnormalEnvironment(appContext, gettext("abnormal")[0]), null, null),
    Triple(PMCommand(appContext, gettext("pmc")[0]), null, null),
    Triple(PMConventionalAPIs(appContext, gettext("pmca")[0]), null, null),
    Triple(PMSundryAPIs(appContext, gettext("pmsa")[0]), null, null),
    Triple(PMQueryIntentActivities(appContext, gettext("pmiq")[0]), null, null),
    Triple(FileDetection(appContext, false, "Libc " + gettext("filedet")[0]), null, null),
    Triple(FileDetection(appContext, true, "Syscall " + gettext("filedet")[0]), null, null),
//    Triple(StatFile(appContext," StatFile "+gettext("filedet")), null, null),
    Triple(XposedModules(appContext, gettext("xposed")[0], false), null, null),
    Triple(XposedModules(appContext, gettext("lspatch")[0], true), null, null),
    Triple(MagiskApp(appContext, gettext("magisk")[0]), null, null),
    Triple(Accessibility(appContext, accList, accenable, gettext("accessibility")[0]), null, null),
    Triple(
        SettingProp(
            appContext,
            development_enable,
            adbenable,
            vpn_connect,
            gettext("settingprops")
        ), null, null
    ),
    Triple(Account(appContext, accountList, gettext("account")[0]), null, null),
)

suspend fun runDetector(id: Int, packages: Collection<String>?) {
    withContext(Dispatchers.IO) {
        val detail = mutableListOf<Pair<String, IDetector.Result>>()
        val result = snapShotList[id].first.run(packages, detail)
        snapShotList[id] = Triple(snapShotList[id].first, result, detail)
    }
}

@Composable
fun MainPage(modifier: Modifier) {
    LaunchedEffect(appContext) {
        //加载自定义
        val preferences = appContext.getSharedPreferences("custom_list", Context.MODE_PRIVATE)
        val customApplications = preferences.getStringSet("list", HashSet())
        customApplications?.forEach {
            basicAppList.add(basicAppList.size, it)
        }
        runDetector(0, null)
        for (i in 1..6) runDetector(i, basicAppList)
        for (i in 7..12) runDetector(i, null)
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState()),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        IconHintCard()
        snapShotList.forEach {
            CheckCard(
                title = it.first.name,
                result = it.second,
                detail = it.third
            )
        }
    }
}
