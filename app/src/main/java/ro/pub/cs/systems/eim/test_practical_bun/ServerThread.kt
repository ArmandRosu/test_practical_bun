package ro.pub.cs.systems.eim.test_practical_bun

import WeatherForecastInformation
import android.util.Log
import java.io.IOException
import java.net.ServerSocket
import java.util.concurrent.ConcurrentHashMap

class ServerThread(private val port: Int) : Thread() {

    private val data: ConcurrentHashMap<String, WeatherForecastInformation> = ConcurrentHashMap()

    @Synchronized
    fun setData(city: String, weatherForecastInformation: WeatherForecastInformation) {
        data[city] = weatherForecastInformation
    }

    @Synchronized
    fun getData(): ConcurrentHashMap<String, WeatherForecastInformation> {
        return data
    }

    private var serverSocket: ServerSocket? = null

    init {
        try {
            serverSocket = ServerSocket(port)
            Log.i(Constants.TAG, "[SERVER THREAD] Server started on port $port")
        } catch (e: IOException) {
            Log.e(Constants.TAG, "[SERVER THREAD] Could not create server socket: ${e.message}")
            if (Constants.DEBUG) e.printStackTrace()
        }
    }

    override fun run() {
        try {
            while (!Thread.currentThread().isInterrupted) {
                val socket = serverSocket?.accept()
                if (socket != null) {
                    Log.i(Constants.TAG, "[SERVER THREAD] A connection request was received")
                    // Pass the shared `data` map to the CommunicationThread
                    val communicationThread = CommunicationThread(socket, this)
                    communicationThread.start()
                }
            }
        } catch (e: IOException) {
            Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred: ${e.message}")
            if (Constants.DEBUG) e.printStackTrace()
        } finally {
            stopThread()
        }
    }

    fun stopThread() {
        interrupt() // Stop the thread
        if (serverSocket != null) {
            try {
                serverSocket?.close()
                Log.i(Constants.TAG, "[SERVER THREAD] Server socket closed")
            } catch (e: IOException) {
                Log.e(Constants.TAG, "[SERVER THREAD] An exception has occurred while closing the socket: ${e.message}")
                if (Constants.DEBUG) e.printStackTrace()
            }
        }
    }
}
