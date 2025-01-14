package ro.pub.cs.systems.eim.test_practical_bun

import android.util.Log
import android.widget.TextView
import java.io.BufferedReader
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket

class ClientThread(
    private val address: String,
    private val port: Int,
    private val city: String,
    private val informationType: String,
    private val weatherForecastTextView: TextView
) : Thread() {

    private var socket: Socket? = null

    override fun run() {
        try {
            // Open socket connection to the server
            socket = Socket(address, port)
            if (socket == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not create socket!")
                return
            }

            // Set up communication streams
            val bufferedReader = socket?.getInputStream()?.bufferedReader()
            val printWriter = socket?.getOutputStream()?.bufferedWriter()?.let { PrintWriter(it, true) }

            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Buffered Reader / Print Writer are null!")
                return
            }

            // Send city and information type to the server
            printWriter.println(city)
            printWriter.flush()
            printWriter.println(informationType)
            printWriter.flush()

            // Read weather information from the server
            var weatherInformation: String?
            while (bufferedReader.readLine().also { weatherInformation = it } != null) {
                val finalizedWeatherInformation = weatherInformation
                // Update the UI with the weather data
                weatherForecastTextView.post {
                    weatherForecastTextView.text = finalizedWeatherInformation
                }
            }

        } catch (ioException: IOException) {
            Log.e(Constants.TAG, "[CLIENT THREAD] An exception has occurred: ${ioException.message}")
            if (Constants.DEBUG) ioException.printStackTrace()
        } finally {
            try {
                socket?.close()
            } catch (ioException: IOException) {
                Log.e(Constants.TAG, "[CLIENT THREAD] Could not close socket: ${ioException.message}")
                if (Constants.DEBUG) ioException.printStackTrace()
            }
        }
    }
}
