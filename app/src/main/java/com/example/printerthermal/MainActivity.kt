package com.example.printerthermal

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.bluetooth.BluetoothSocket
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.app.ActivityCompat
import java.util.UUID
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {

    private val requestEnableBluetooth = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {

            val myViewModel: MainViewModel =
                viewModel(
                    factory = MainViewModelFactory()
                )

            Scaffold(modifier = Modifier.fillMaxSize()) { innerPadding ->
                Column(modifier = Modifier.padding(innerPadding)) {
                    myViewModel.printerSelected.collectAsState(Print("", "")).value.let { value ->
                        Greeting(
                            name = " ${value.name} - ${value.address}",
                        )
                    }
                    MyButton(myViewModel)
                    Box(
                        modifier = Modifier.fillMaxSize()
                    ) {
                        myViewModel.listPrinter.collectAsState(mutableListOf<Print>()).value.let { value ->
                            LazyColumn {
                                items(value) { data ->
                                    Text(
                                        text = "${data.name} - ${data.address}",
                                        modifier = Modifier
                                            .padding(2.dp, 4.dp)
                                            .clickable {
                                                myViewModel.updatePrinterSelected(
                                                    Print(
                                                        data.name,
                                                        data.address
                                                    )
                                                )
                                            }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            enableBluetooth(this, myViewModel)

        }

    }

    @Composable
    fun MyButton(myViewModel: MainViewModel) {
        val context = LocalContext.current
        Button(onClick = {
            enableBluetooth(context, myViewModel)
        }) {
            Text(text = "Scan Pairing Bluetooth")
        }
        Button(onClick = {
            testPrint(myViewModel)
        }) {
            Text(text = "Test Print")
        }
    }

    private fun enableBluetooth(context: Context, myViewModel: MainViewModel) {
        Log.d("Bluetooth", "enableBluetooth click")
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        if (!bluetoothAdapter.isEnabled) {
            Log.d("Bluetooth", "Bluetooth disable")
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.BLUETOOTH_CONNECT
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                Log.d("Bluetooth", "enableBluetooth permission")
                //Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                val enableBtIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
                startActivityForResult(enableBtIntent, requestEnableBluetooth)
                return
            }
        } else {
            Log.d("Bluetooth", "enableBluetooth")
            getPairedDevices(context, myViewModel)
        }
    }

    private fun getPairedDevices(context: Context, myViewModel: MainViewModel) {

        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.adapter

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.BLUETOOTH_CONNECT
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.

            val pairedDevices = bluetoothAdapter.bondedDevices
            val pairedDevicesList = pairedDevices.toList()
            // Tampilkan daftar perangkat terpasang
            val listPrint = mutableListOf<Print>()
            for (device in pairedDevicesList) {
                Log.d("Bluetooth", "${device.name} - ${device.address}")
                listPrint.add(Print(device.name, device.address))
            }
            myViewModel.updatePrinterList(listPrint)
            return
        }

    }

    private fun setPrinter() {

    }

    private fun connectToBluetooth(context: Context) {
        val bluetoothManager =
            context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
        val bluetoothAdapter = bluetoothManager.getAdapter()

        val bluetoothDevice = bluetoothAdapter.getRemoteDevice("idPrinter")


    }

    private fun testPrint(myViewModel: MainViewModel) {
        if (myViewModel.printerSelected.value.address == "") {
            Toast.makeText(this, "Pilih Printernya Gan", Toast.LENGTH_LONG).show()
        } else {
            var bluetoothSocket: BluetoothSocket
            val applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
        }

    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "$name",
        modifier = modifier
    )
}