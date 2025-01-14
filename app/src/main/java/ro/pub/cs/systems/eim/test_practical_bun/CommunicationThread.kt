package ro.pub.cs.systems.eim.test_practical_bun

import WeatherForecastInformation
import android.util.Log
import org.json.JSONObject
import org.jsoup.Jsoup
import java.io.BufferedReader
import java.io.IOException
import java.io.PrintWriter
import java.net.Socket
import java.util.concurrent.ConcurrentHashMap

class CommunicationThread(
    private val socket: Socket,
    private val serverThread: ServerThread
) : Thread() {

    override fun run() {
        if (socket == null) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] Socket is null!")
            return
        }

        try {
            // Get input and output streams
            val bufferedReader: BufferedReader? = Utilities.getReader(socket)
            val printWriter: PrintWriter? = Utilities.getWriter(socket)

            if (bufferedReader == null || printWriter == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Buffered Reader / Print Writer are null!")
                return
            }

            Log.i(Constants.TAG, "[COMMUNICATION THREAD] Waiting for parameters from client (city / information type)...")
            val city = bufferedReader.readLine()
            val informationType = bufferedReader.readLine()

            if (city.isNullOrEmpty() || informationType.isNullOrEmpty()) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error receiving parameters from client (city / information type)!")
                return
            }

            // Retrieve or fetch weather data
            val data: ConcurrentHashMap<String, WeatherForecastInformation> = serverThread.getData()
            var weatherForecastInformation: WeatherForecastInformation? = null

            if (data.containsKey(city)) {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the cache...")
                weatherForecastInformation = data[city]
            } else {
                Log.i(Constants.TAG, "[COMMUNICATION THREAD] Getting the information from the web service...")

                val apiKey = Constants.API_KEY
                val url = "${Constants.WEB_SERVICE_ADDRESS}?q=$city&appid=$apiKey"

                try {
                    val response = Jsoup.connect(url).ignoreContentType(true).execute().body()
                    val jsonObject = JSONObject(response)

                    val main = jsonObject.getJSONObject("main")
                    val temperature = main.getDouble("temp").toString()
                    val pressure = main.getInt("pressure").toString()
                    val humidity = main.getInt("humidity").toString()

                    val wind = jsonObject.getJSONObject("wind")
                    val windSpeed = wind.getDouble("speed").toString()

                    val weatherArray = jsonObject.getJSONArray("weather")
                    val condition = weatherArray.getJSONObject(0).getString("description")

                    weatherForecastInformation = WeatherForecastInformation(
                        temperature = temperature,
                        windSpeed = windSpeed,
                        condition = condition,
                        pressure = pressure,
                        humidity = humidity
                    )

                    serverThread.setData(city, weatherForecastInformation)
                } catch (e: Exception) {
                    Log.e(Constants.TAG, "[COMMUNICATION THREAD] Error getting information from the web service: ${e.message}")
                    if (Constants.DEBUG) e.printStackTrace()
                    return
                }
            }

            if (weatherForecastInformation == null) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Weather Forecast Information is null!")
                return
            }

            // Build the response based on the requested information type
            val result = when (informationType) {
                Constants.ALL -> weatherForecastInformation.toString()
                Constants.TEMPERATURE -> "Temperature: ${weatherForecastInformation.temperature}K"
                Constants.WIND_SPEED -> "Wind Speed: ${weatherForecastInformation.windSpeed} m/s"
                Constants.CONDITION -> "Condition: ${weatherForecastInformation.condition}"
                Constants.PRESSURE -> "Pressure: ${weatherForecastInformation.pressure} hPa"
                Constants.HUMIDITY -> "Humidity: ${weatherForecastInformation.humidity}%"
                else -> "[COMMUNICATION THREAD] Wrong information type (all / temperature / wind_speed / condition / humidity / pressure)!"
            }

            // Send the response to the client
            printWriter.println(result)
            printWriter.flush()

        } catch (e: IOException) {
            Log.e(Constants.TAG, "[COMMUNICATION THREAD] An exception occurred: ${e.message}")
            if (Constants.DEBUG) e.printStackTrace()
        } finally {
            try {
                socket.close()
            } catch (e: IOException) {
                Log.e(Constants.TAG, "[COMMUNICATION THREAD] Could not close socket: ${e.message}")
                if (Constants.DEBUG) e.printStackTrace()
            }
        }
    }
}
