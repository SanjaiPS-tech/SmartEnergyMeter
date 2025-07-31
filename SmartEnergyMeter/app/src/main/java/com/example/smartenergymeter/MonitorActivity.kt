package com.sanjai.smartenergymeter

import android.bluetooth.BluetoothAdapter
import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothSocket
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.data.*
import java.io.InputStream
import java.util.*
import kotlin.concurrent.thread

class MonitorActivity : AppCompatActivity() {

    private lateinit var chart: LineChart
    private lateinit var statusText: TextView
    private lateinit var readingText: TextView

    private lateinit var voltageData: LineDataSet
    private lateinit var currentData: LineDataSet
    private lateinit var powerData: LineDataSet

    private lateinit var btSocket: BluetoothSocket
    private var inputStream: InputStream? = null

    private val uuid: UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB") // SPP UUID

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monitor)

        chart = findViewById(R.id.chart)
        statusText = findViewById(R.id.statusText)
        readingText = findViewById(R.id.readingText)

        setupChart()
        connectBluetooth()
    }

    private fun setupChart() {
        voltageData = LineDataSet(ArrayList(), "Voltage (V)")
        voltageData.color = getColor(R.color.holo_blue_light)
        voltageData.setDrawCircles(false)

        currentData = LineDataSet(ArrayList(), "Current (A)")
        currentData.color = getColor(R.color.holo_green_light)
        currentData.setDrawCircles(false)

        powerData = LineDataSet(ArrayList(), "Power (W)")
        powerData.color = getColor(R.color.holo_red_light)
        powerData.setDrawCircles(false)

        val lineData = LineData(voltageData, currentData, powerData)
        chart.data = lineData
        chart.legend.verticalAlignment = Legend.LegendVerticalAlignment.TOP
        chart.description.isEnabled = false
    }

    private fun connectBluetooth() {
        val deviceAddress = intent.getStringExtra("device_address")
        val btDevice: BluetoothDevice = BluetoothAdapter.getDefaultAdapter().getRemoteDevice(deviceAddress)

        thread {
            try {
                btSocket = btDevice.createRfcommSocketToServiceRecord(uuid)
                btSocket.connect()
                inputStream = btSocket.inputStream
                runOnUiThread {
                    statusText.text = "Connected to ${btDevice.name}"
                }

                val buffer = ByteArray(1024)
                var bufferStr = ""

                while (true) {
                    val bytes = inputStream?.read(buffer) ?: break
                    bufferStr += String(buffer, 0, bytes)
                    if (bufferStr.contains("\n")) {
                        val lines = bufferStr.split("\n")
                        for (i in 0 until lines.size - 1) {
                            processLine(lines[i])
                        }
                        bufferStr = lines.last()
                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    statusText.text = "Connection failed: ${e.message}"
                }
            }
        }
    }

    private var timeIndex = 0f

    private fun processLine(line: String) {
        val regex = Regex("V:([0-9.]+),I:([0-9.]+),P:([0-9.]+)")
        val match = regex.find(line.trim()) ?: return

        val voltage = match.groupValues[1].toFloat()
        val current = match.groupValues[2].toFloat()
        val power = match.groupValues[3].toFloat()

        runOnUiThread {
            readingText.text = "V: $voltage V\nI: $current A\nP: $power W"

            chart.data.addEntry(Entry(timeIndex, voltage), 0)
            chart.data.addEntry(Entry(timeIndex, current), 1)
            chart.data.addEntry(Entry(timeIndex, power), 2)

            timeIndex += 1f

            chart.data.notifyDataChanged()
            chart.notifyDataSetChanged()
            chart.invalidate()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        try {
            btSocket.close()
        } catch (e: Exception) {
        }
    }
}
