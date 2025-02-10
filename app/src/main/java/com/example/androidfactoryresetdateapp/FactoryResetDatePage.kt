import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.provider.Settings
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@SuppressLint("QueryPermissionsNeeded")
@Composable
fun FactoryResetDateScreen(padding: Modifier) {
    val context = LocalContext.current
    val factoryResetDate by remember { mutableStateOf(getFactoryResetDate(context)) }
    val isRooted by remember { mutableStateOf(isDeviceRooted()) }

    Scaffold(
        topBar = {
            TopAppBar(title = { Text("Factory Reset Date") })
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            Text(text = "Estimated Factory Reset Date:", style = MaterialTheme.typography.headlineMedium)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = factoryResetDate, style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = "Device Rooted: ${if (isRooted) "Yes" else "No"}", style = MaterialTheme.typography.bodyLarge)
        }
    }
}

@SuppressLint("HardwareIds")
fun getFactoryResetDate(context: Context): String {
    // 1. Try getting Android ID (resets after factory reset)
    val androidId = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
    val androidIdResetDate = getApproximateDateFromAndroidId(androidId)

    // 2. Try getting first app install time as a fallback
    val firstInstallDate = getFirstInstallDate(context)

    return "Android ID Reset: $androidIdResetDate\nFirst App Install: $firstInstallDate"
}

private fun getApproximateDateFromAndroidId(androidId: String): String {
    return try {
        val timestamp = androidId.hashCode().toLong().coerceAtLeast(0) * 1000
        formatDate(timestamp)
    } catch (e: Exception) {
        "Unknown"
    }
}

private fun getFirstInstallDate(context: Context): String {
    return try {
        val packageManager = context.packageManager
        val packageInfo = packageManager.getPackageInfo("com.android.vending", 0)
        formatDate(packageInfo.firstInstallTime)
    } catch (e: PackageManager.NameNotFoundException) {
        "Unknown"
    }
}

private fun formatDate(timestamp: Long): String {
    return if (timestamp > 0) {
        val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        formatter.format(Date(timestamp))
    } else {
        "Unknown"
    }
}

fun isDeviceRooted(): Boolean {
    return try {
        val paths = arrayOf(
            "/system/bin/su",
            "/system/xbin/su",
            "/sbin/su",
            "/system/su",
            "/system/bin/.ext/.su",
            "/system/usr/we-need-root/su-backup",
            "/system/xbin/mu"
        )
        paths.any { File(it).exists() }
    } catch (e: Exception) {
        false
    }
}
