package com.byxiaorun.detector

import android.accessibilityservice.AccessibilityServiceInfo
import android.accounts.Account
import android.accounts.AccountManager
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Context.CONNECTIVITY_SERVICE
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.util.Log
import android.view.accessibility.AccessibilityManager
import android.widget.AdapterView
import android.widget.AdapterView.OnItemClickListener
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.EmojiObjects
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.byxiaorun.detector.BuildConfig
import com.byxiaorun.detector.MyApplication.Companion.accountList
import com.byxiaorun.detector.MyApplication.Companion.appContext
import com.byxiaorun.detector.MyApplication.Companion.topActivity
import com.byxiaorun.detector.MyApplication.Companion.vpn_connect
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import icu.nullptr.applistdetector.MainPage
import icu.nullptr.applistdetector.theme.MyTheme
import java.io.*
import java.net.NetworkInterface
import java.util.*
import kotlin.collections.HashSet
import androidx.core.content.edit
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout

/**
 *Created by byxiaorun on 2022/4/20/0020.
 */
class MainActivity : ComponentActivity() {
    @OptIn(ExperimentalMaterial3Api::class)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkDisabled()
        checkSetting()
        Accounts()
        setContent {
            MyTheme {
                var showDialog by remember { mutableStateOf(false) }
                if (showDialog) AboutDialog { showDialog = false }
                Scaffold(
                    topBar = { MainTopBar() },
                    floatingActionButton = { MainFab { showDialog = true } },
                ) { innerPadding ->
                    MainPage(Modifier.padding(innerPadding))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)

@Composable
private fun MainTopBar() {
    CenterAlignedTopAppBar(
        title = {
            Text(stringResource(id = R.string.app_name))
        },
        actions = {
            IconButton(onClick = {
                showCustomTargetSheet()
            }) {
                Icon(
                    imageVector = Icons.Filled.Edit,
                    contentDescription = "list setting"
                )
            }
        }
    )
}

@Composable
private fun MainFab(onClick: () -> Unit) {
    ExtendedFloatingActionButton(
        icon = { Icon(Icons.Outlined.EmojiObjects, (stringResource(id = R.string.about))) },
        text = { Text(stringResource(id = R.string.about)) },
        onClick = onClick
    )
}

@Composable
private fun AboutDialog(onDismiss: () -> Unit) {
    val context = LocalContext.current
    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(android.R.string.ok))
            }
        },
        title = { Text(stringResource(id = R.string.about)) },
        text = {
            Column(horizontalAlignment = Alignment.Start) {
                CompositionLocalProvider(LocalTextStyle provides MaterialTheme.typography.bodyLarge) {
                    Text(stringResource(R.string.app_name) + " V${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})")
                    Text(stringResource(R.string.authored) + ": Nullptr & byxiaorun & NativeStar")
                }
                Spacer(Modifier.height(10.dp))
                val annotatedString = buildAnnotatedString {
                    pushStringAnnotation("GitHub", "https://github.com/NativeStar/Ruru")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(appContext.getString(R.string.source))
                    }
                    pop()
                    append("  ")
                    pushStringAnnotation("Telegram", "https://t.me/HideMyApplist")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(appContext.getString(R.string.telegram))
                    }
                    pop()
                    append("  ")
                    pushStringAnnotation("Telegram", "https://t.me/xrshop")
                    withStyle(SpanStyle(color = MaterialTheme.colorScheme.primary)) {
                        append(appContext.getString(R.string.telegram))
                    }
                }
                ClickableText(
                    annotatedString,
                    style = MaterialTheme.typography.bodyLarge
                ) { offset ->
                    annotatedString.getStringAnnotations("GitHub", offset, offset).firstOrNull()
                        ?.let {
                            ContextCompat.startActivity(
                                context,
                                Intent(Intent.ACTION_VIEW, Uri.parse(it.item)),
                                null
                            )
                        }
                    annotatedString.getStringAnnotations("Telegram", offset, offset).firstOrNull()
                        ?.let {
                            ContextCompat.startActivity(
                                context,
                                Intent(Intent.ACTION_VIEW, Uri.parse(it.item)),
                                null
                            )
                        }
                }
            }
        },
    )
}

fun gettext(string: String): Array<String> {
    return when (string) {
        "not_found" -> arrayOf(appContext.getString(R.string.not_found))
        "method" -> arrayOf(appContext.getString(R.string.method))
        "suspicious" -> arrayOf(appContext.getString(R.string.suspicious))
        "found" -> arrayOf(appContext.getString(R.string.found))
        "abnormal" -> arrayOf(appContext.getString(R.string.abnormal))
        "filedet" -> arrayOf(appContext.getString(R.string.filedet))
        "pmc" -> arrayOf(appContext.getString(R.string.pmc))
        "pmca" -> arrayOf(appContext.getString(R.string.pmca))
        "pmsa" -> arrayOf(appContext.getString(R.string.pmsa))
        "zcd" -> arrayOf(appContext.getString(R.string.zcd))
        "pmiq" -> arrayOf(appContext.getString(R.string.pmiq))
        "xposed" -> arrayOf(appContext.getString(R.string.xposed))
        "lspatch" -> arrayOf(appContext.getString(R.string.lspatch))
        "magisk" -> arrayOf(appContext.getString(R.string.magisk))
        "accessibility" -> arrayOf(appContext.getString(R.string.accessibility))
        "settingprops" -> appContext.resources.getStringArray(R.array.settingprops)
        "account" -> arrayOf(appContext.getString(R.string.account))
        else -> arrayOf("none")
    }
}


private fun checkDisabled() {
    MyApplication.accList = getFromAccessibilityManager() + getFromSettingsSecure()
}

private fun getFromAccessibilityManager(): List<String> {
    val accessibilityManager =
        ContextCompat.getSystemService(appContext, AccessibilityManager::class.java)
            ?: error("unreachable")
    val serviceList: List<AccessibilityServiceInfo> =
        accessibilityManager.getEnabledAccessibilityServiceList(AccessibilityServiceInfo.FEEDBACK_ALL_MASK)
            ?: emptyList()
    val nameList = serviceList.map {
        appContext.packageManager.getApplicationLabel(it.resolveInfo.serviceInfo.applicationInfo)
            .toString()
    }.toMutableList()
    if (accessibilityManager.isEnabled) {
        nameList.add("AccessibilityManager.isEnabled")
    }
    if (accessibilityManager.isTouchExplorationEnabled) {
        nameList.add("AccessibilityManager.isTouchExplorationEnabled")
    }
    return nameList
}

private fun getFromSettingsSecure(): List<String> {
    try {
        val settingValue = Settings.Secure.getString(
            appContext.contentResolver,
            Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES
        )
        val nameList = if (settingValue.isNullOrEmpty()) {
            emptyList()
        } else {
            settingValue.split(':')
        }.toMutableList()
        val enabled = Settings.Secure.getInt(
            appContext.contentResolver,
            Settings.Secure.ACCESSIBILITY_ENABLED
        )
        if (enabled != 0) {
            MyApplication.accenable = true
        }
        return nameList
    } catch (e: Settings.SettingNotFoundException) {
        return emptyList()
    }

}

fun showCustomTargetSheet() {
    val sheet = BottomSheetDialog(topActivity)
    sheet.setTitle("debug")
    //显示列表
    val contentView = LayoutInflater.from(topActivity).inflate(R.layout.package_list_layout, null);
    val listView = contentView.findViewById<ListView>(R.id.packageList)
    val pref = appContext.getSharedPreferences("custom_list", Context.MODE_PRIVATE)
    val list = pref.getStringSet("list", HashSet<String>())?.toMutableList()
    listView.adapter = ArrayAdapter(topActivity, android.R.layout.simple_list_item_1, list!!)
    listView.setOnItemClickListener(object : OnItemClickListener {
        override fun onItemClick(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
            val pkgName = list[position] as String
            list.remove(pkgName)
            //保存
            pref.edit().putStringSet("list", java.util.HashSet(list)).apply()
            (listView.adapter as ArrayAdapter<String>).notifyDataSetChanged()
        }
    })
    contentView.findViewById<Button>(R.id.add_package_button).setOnClickListener {
        val view = LayoutInflater.from(topActivity).inflate(R.layout.package_input_layout, null)
        val textInput = view.findViewById<TextInputEditText>(R.id.package_name_input)
        MaterialAlertDialogBuilder(topActivity).setTitle(
            appContext.getString(
                R.string.dialog_add_package
            )
        )
            .setView(view)
            .setNeutralButton(appContext.getString(R.string.text_cancel), null)
            .setPositiveButton(
                appContext.getString(R.string.text_add),
                object : DialogInterface.OnClickListener {
                    override fun onClick(dialog: DialogInterface?, which: Int) {
                        val textRaw = textInput.text.toString()
                        if (textRaw.isBlank()) return
                        list.add(list.size, textRaw)
                        pref.edit { putStringSet("list", java.util.HashSet(list)) }
                        (listView.adapter as ArrayAdapter<*>).notifyDataSetChanged()
                    }
                }).show()
    }
    sheet.setContentView(contentView)
    sheet.show()
}

fun checkSetting() {
    if ((Settings.Secure.getInt(
            appContext.contentResolver,
            Settings.Global.DEVELOPMENT_SETTINGS_ENABLED,
            0
        ) == 1)
    ) {
        MyApplication.development_enable = true
    }
    if ((Settings.Secure.getInt(appContext.contentResolver, Settings.Global.ADB_ENABLED, 0) == 1

                )
    ) {
        MyApplication.adbenable = true
    }

    try {
        vpn_connect = NetworkInterface.getNetworkInterfaces()?.toList()
            ?.any { it.isUp && it.interfaceAddresses.isNotEmpty() && (it.name == "tun0" || it.name == "ppp0") } == true ||
                (appContext.getSystemService(CONNECTIVITY_SERVICE) as ConnectivityManager).getNetworkInfo(
                    17
                )?.isConnectedOrConnecting == true ||
                (!System.getProperty("http.proxyHost")
                    .isNullOrEmpty() && (System.getProperty("http.proxyPort")?.toIntOrNull()
                    ?: -1) != -1)
    } catch (e: Throwable) {
        e.printStackTrace()
    }
}


fun Accounts() {
    try {
        accountList = listOf()
        val accounts = AccountManager.get(appContext).getAccounts()
        val mutableList: MutableList<String> = mutableListOf()

        if (accounts.isNotEmpty()) {
            for (account in accounts) {
                val accountType = account.type
                val accountName = account.name
                mutableList.add("$accountType, $accountName")
            }
            accountList = mutableList.toList()
        }
    } catch (e: Exception) {
        Log.e("Accounts", "Error while retrieving accounts: ${e.message}", e)
        accountList = listOf()
    }
}
