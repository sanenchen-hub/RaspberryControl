/**
 * @author sanenchen
 * @details 主界面
 */
package com.sanenchen.raspberrycontrol.ui.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment.Companion.CenterEnd
import androidx.compose.ui.Alignment.Companion.CenterStart
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sanenchen.raspberrycontrol.ui.theme.RaspberryControlTheme
import com.sanenchen.raspberrycontrol.utils.DataUtils
import org.json.JSONObject
import java.net.URL

class MainActivity : ComponentActivity() {
    var threadOnlyOne = true // 保证有且只有一个线程
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RaspberryControlTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        TopAppBar(title = { Text("RaspberryControl") }, actions = {
                            IconButton(onClick = {
                                startActivity(
                                    Intent(
                                        this@MainActivity,
                                        SettingActivity().javaClass
                                    )
                                )
                            }) {
                                Icon(Icons.Filled.Settings, "设置")
                            }
                        }) // TopAppBar
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .verticalScroll(rememberScrollState())
                        ) {
                            RaspiBasicInfo() // 树莓派基本信息
                        }
                    }
                }
            }
        }
    }

    // 树莓派的基本信息
    @Composable
    fun RaspiBasicInfo() {
        val mContext = LocalContext.current

        DataUtils.recycleTime =
            mContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
                .getLong("RecycleTime", 500)

        val cpuTemp = remember { mutableStateOf("0℃") }
        val cpuFREQ = remember { mutableStateOf("0Mhz") }
        val memALL = remember { mutableStateOf("0Mb") }
        val memUSED = remember { mutableStateOf("0Mb") }
        val errorDialog = remember { mutableStateOf(false) }
        Card(
            elevation = 4.dp, modifier = Modifier
                .padding(top = 8.dp, start = 16.dp, end = 16.dp)
                .fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(
                    top = 8.dp,
                    bottom = 8.dp,
                    start = 8.dp,
                    end = 8.dp
                )
            ) {
                Box(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        "Raspi 基本信息",
                        fontWeight = FontWeight.W700,
                        modifier = Modifier.align(CenterStart)
                    )
                    CircularProgressIndicator(
                        modifier = Modifier
                            .align(CenterEnd)
                            .size(18.dp)
                    )
                }
                Box(Modifier.padding(top = 8.dp, start = 8.dp)) {
                    Text("CPU 温度", fontWeight = FontWeight.W300, fontSize = 14.sp)
                    Text(
                        cpuTemp.value,
                        fontWeight = FontWeight.W300,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 128.dp)
                    )
                }
                Box(Modifier.padding(start = 8.dp, top = 4.dp)) {
                    Text("CPU 频率", fontWeight = FontWeight.W300, fontSize = 14.sp)
                    Text(
                        cpuFREQ.value,
                        fontWeight = FontWeight.W300,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 128.dp)
                    )
                }
                Box(Modifier.padding(start = 8.dp, top = 4.dp)) {
                    Text("已安装的内存", fontWeight = FontWeight.W300, fontSize = 14.sp)
                    Text(
                        memALL.value,
                        fontWeight = FontWeight.W300,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 128.dp)
                    )
                }
                Box(Modifier.padding(start = 8.dp, top = 4.dp)) {
                    Text("已使用的内存", fontWeight = FontWeight.W300, fontSize = 14.sp)
                    Text(
                        memUSED.value,
                        fontWeight = FontWeight.W300,
                        fontSize = 14.sp,
                        modifier = Modifier.padding(start = 128.dp)
                    )
                }
            }
        }
        // 数据获取解析
        if (threadOnlyOne) // 保证有且只有一个线程
            Thread {
                try {
                    threadOnlyOne = false
                    while (true) {
                        Thread.sleep(DataUtils.recycleTime) // 休眠
                        // 获取数据
                        val result = URL("${DataUtils.raspiURL}/raspi_base_info").readText()
                        // 解析数据
                        val jsonObject = JSONObject(result)
                        // 放入数据
                        cpuTemp.value = "${jsonObject.getInt("CPU_TEMP") / 1000f}℃"
                        cpuFREQ.value = "${jsonObject.getInt("CPU_FREQ") / 1000000}Mhz"
                        memALL.value = "${jsonObject.getInt("MEM_ALL") / 1000}M"
                        memUSED.value = "${jsonObject.getInt("MEM_USED") / 1000}M"
                    }
                } catch (e: Exception) {
                    errorDialog.value = true
                }

            }.start()

        // 网络错误提示框
        if (errorDialog.value) {
            AlertDialog(
                onDismissRequest = { errorDialog.value = !errorDialog.value },
                title = { Text("错误", fontWeight = FontWeight.W700) },
                text = { Text("检查网络连接", fontSize = 16.sp) },
                confirmButton = {
                    TextButton(onClick = { errorDialog.value = !errorDialog.value }) {
                        Text(text = "好的", style = MaterialTheme.typography.button)
                    }
                }
            )
        }
    }
}