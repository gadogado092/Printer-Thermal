package com.example.printerthermal

import android.Manifest
import android.R.attr.data
import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.lifecycle.viewmodel.compose.viewModel
import java.io.File
import java.io.FileOutputStream
import java.util.UUID


class MainActivity : ComponentActivity() {

    private val requestEnableBluetootha = 1

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
            testPrint(context, myViewModel)
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
                startActivityForResult(enableBtIntent, requestEnableBluetootha)
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

    private fun testPrint(context: Context, myViewModel: MainViewModel) {
        if (myViewModel.printerSelected.value.address == "") {
            Toast.makeText(this, "Pilih Printernya Gan", Toast.LENGTH_LONG).show()
        } else {
            val bluetoothManager =
                context.getSystemService(Context.BLUETOOTH_SERVICE) as BluetoothManager
            val bluetoothAdapter = bluetoothManager.adapter

            if (bluetoothAdapter == null) {
                Toast.makeText(this, "Bluetooth Adapter Broken", Toast.LENGTH_LONG).show()
            } else {
                val applicationUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB")
                val bluetoothDevice =
                    bluetoothAdapter.getRemoteDevice(myViewModel.printerSelected.value.address)

                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.BLUETOOTH_CONNECT
                    ) != PackageManager.PERMISSION_GRANTED
                ) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        ActivityCompat.requestPermissions(
                            this,
                            arrayOf(Manifest.permission.BLUETOOTH_CONNECT),
                            1
                        )
                    }
//                    Toast.makeText(this, "Printer Permission Problem 1", Toast.LENGTH_LONG)
//                        .show()
                }


                val bluetoothSocket =
                    bluetoothDevice.createRfcommSocketToServiceRecord(applicationUUID)
                //check koneksi
                try {
                    bluetoothSocket.connect()
                } catch (e: Exception) {
                    Toast.makeText(
                        this@MainActivity,
                        "Printer Socket Tidak Tersambung",
                        Toast.LENGTH_LONG
                    )
                        .show()
                    bluetoothSocket.close()
                    return
                }

                try {

                    if (bluetoothSocket == null) {
                        Toast.makeText(
                            this@MainActivity,
                            "Printer Socket Tidak Tersambung",
                            Toast.LENGTH_LONG
                        )
                            .show()
                        return
                    } else {
                        Toast.makeText(this, "Printer Mencetak", Toast.LENGTH_LONG)
                            .show()

                        val data = "Selamat Datang Boss"

                        val outputStream = bluetoothSocket.outputStream

                        val printformat = byteArrayOf(0x1B, 0x21, 0x03)
                        outputStream.write(printformat)

                        val cc = byteArrayOf(0x1B, 0x21, 0x03)
                        outputStream.write(cc)

                        val ESC_ALIGN_LEFT = byteArrayOf(0x1b, 'a'.code.toByte(), 0x00)
                        outputStream.write(ESC_ALIGN_LEFT)

                        val bytes = data.toByteArray()

                        outputStream.write(bytes)

                        val LF = byteArrayOf(0x0A)
                        outputStream.write(LF)
                        outputStream.write(LF)
                        outputStream.write(LF)
                        outputStream.write(LF)

                        outputStream.flush()
                        outputStream.close()
                        bluetoothSocket.close()


                    }

                } catch (e: Exception) {
                    Log.e("bluetooth", e.message.toString())
                    Toast.makeText(this@MainActivity, "Error ${e.message}", Toast.LENGTH_LONG)
                        .show()
                }


            }

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