/**
 * @author sanenchen
 * @details 设置界面
 */
package com.sanenchen.raspberrycontrol.ui.activities

import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sanenchen.raspberrycontrol.ui.theme.RaspberryControlTheme
import com.sanenchen.raspberrycontrol.utils.DataUtils

class SettingActivity : ComponentActivity() {
    @ExperimentalAnimationApi
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RaspberryControlTheme {
                // A surface container using the 'background' color from the theme
                Surface(
                    color = MaterialTheme.colors.background,
                    modifier = Modifier.fillMaxSize()
                ) {
                    Column {
                        TopAppBar(
                            title = { Text(text = "设置") },
                            navigationIcon = {
                                IconButton(onClick = { finish() }) {
                                    Icon(
                                        Icons.Filled.ArrowBack,
                                        "返回",
                                    )
                                }
                            })
                        Column {
                            SetRecycleTime()
                        }
                    }
                }
            }
        }
    }

    @ExperimentalAnimationApi
    @Composable
    fun SetRecycleTime() {
        val mContext = LocalContext.current
        val recycleTime = remember {
            mutableStateOf(
                mContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
                    .getLong("RecycleTime", 500).toString()
            )
        }
        val showOKButton = remember { mutableStateOf(false) }
        Card(elevation = 4.dp, modifier = Modifier.padding(top = 8.dp, start = 16.dp, end = 16.dp)) {
            Column(
                Modifier
                    .padding(all = 8.dp)
                    .fillMaxWidth()
            ) {
                Text("查询间隔时间", fontWeight = FontWeight.W700)
                OutlinedTextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    value = recycleTime.value,
                    onValueChange = {
                        showOKButton.value = true
                        recycleTime.value = it
                        if (it.isEmpty())
                            showOKButton.value = false
                    },
                    trailingIcon = {
                        Text("ms")
                    },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                AnimatedVisibility(visible = showOKButton.value) {
                    Box(modifier = Modifier.fillMaxWidth()) {
                        OutlinedButton(
                            modifier = Modifier
                                .padding(top = 8.dp)
                                .align(Alignment.CenterEnd),
                            onClick = {
                                mContext.getSharedPreferences("settings", Context.MODE_PRIVATE)
                                    .edit().putLong("RecycleTime", recycleTime.value.toLong()).apply()
                                DataUtils.recycleTime = recycleTime.value.toLong()
                                showOKButton.value = false
                            }) {
                            Text(text = "修改")
                        }
                    }
                }
            }
        }
    }
}