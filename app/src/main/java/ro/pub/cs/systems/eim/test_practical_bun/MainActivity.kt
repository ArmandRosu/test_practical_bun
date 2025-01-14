//package ro.pub.cs.systems.eim.test_practical_bun
//
//import android.os.Bundle
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//
//class MainActivity : AppCompatActivity() {
//
//    // Declare views
//    private lateinit var serverPortEditText: EditText
//    private lateinit var connectButton: Button
//    private lateinit var clientAddressEditText: EditText
//    private lateinit var clientPortEditText: EditText
//    private lateinit var cityEditText: EditText
//    private lateinit var informationTypeSpinner: Spinner
//    private lateinit var getWeatherForecastButton: Button
//    private lateinit var weatherForecastTextView: TextView
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // Initialize views
//        serverPortEditText = findViewById(R.id.server_port_edit_text)
//        connectButton = findViewById(R.id.connect_button)
//        clientAddressEditText = findViewById(R.id.client_address_edit_text)
//        clientPortEditText = findViewById(R.id.client_port_edit_text)
//        cityEditText = findViewById(R.id.city_edit_text)
//        informationTypeSpinner = findViewById(R.id.information_type_spinner)
//        getWeatherForecastButton = findViewById(R.id.get_weather_forecast_button)
//        weatherForecastTextView = findViewById(R.id.weather_forecast_text_view)
//
//        // Set up listeners
//        connectButton.setOnClickListener {
//            val serverPort = serverPortEditText.text.toString()
//            if (serverPort.isNotEmpty()) {
//                Toast.makeText(this, "Connecting to server on port $serverPort", Toast.LENGTH_SHORT).show()
//                // Implement your server connection logic here
//            } else {
//                Toast.makeText(this, "Please enter a server port", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        getWeatherForecastButton.setOnClickListener {
//            val clientAddress = clientAddressEditText.text.toString()
//            val clientPort = clientPortEditText.text.toString()
//            val city = cityEditText.text.toString()
//            val informationType = informationTypeSpinner.selectedItem.toString()
//
//            if (clientAddress.isNotEmpty() && clientPort.isNotEmpty() && city.isNotEmpty()) {
//                // Display a toast for now
//                Toast.makeText(
//                    this,
//                    "Fetching weather forecast for $city with type $informationType",
//                    Toast.LENGTH_SHORT
//                ).show()
//                // Implement your weather forecast fetching logic here
//                weatherForecastTextView.text = "Sample weather forecast for $city"
//            } else {
//                Toast.makeText(this, "Please fill in all client details", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//}

//
//package ro.pub.cs.systems.eim.test_practical_bun
//
//import android.os.Bundle
//import android.widget.*
//import androidx.appcompat.app.AppCompatActivity
//import kotlinx.coroutines.CoroutineScope
//import kotlinx.coroutines.Dispatchers
//import kotlinx.coroutines.launch
//import kotlinx.coroutines.withContext
//import okhttp3.OkHttpClient
//import okhttp3.Request
//import org.json.JSONObject
//import java.io.PrintWriter
//import java.net.Socket
//
//class MainActivity : AppCompatActivity() {
//
//    private lateinit var serverPortEditText: EditText
//    private lateinit var connectButton: Button
//    private lateinit var clientAddressEditText: EditText
//    private lateinit var clientPortEditText: EditText
//    private lateinit var cityEditText: EditText
//    private lateinit var informationTypeSpinner: Spinner
//    private lateinit var getWeatherForecastButton: Button
//    private lateinit var weatherForecastTextView: TextView
//
//    private val httpClient = OkHttpClient()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContentView(R.layout.activity_main)
//
//        // Initialize views
//        serverPortEditText = findViewById(R.id.server_port_edit_text)
//        connectButton = findViewById(R.id.connect_button)
//        clientAddressEditText = findViewById(R.id.client_address_edit_text)
//        clientPortEditText = findViewById(R.id.client_port_edit_text)
//        cityEditText = findViewById(R.id.city_edit_text)
//        informationTypeSpinner = findViewById(R.id.information_type_spinner)
//        getWeatherForecastButton = findViewById(R.id.get_weather_forecast_button)
//        weatherForecastTextView = findViewById(R.id.weather_forecast_text_view)
//
//        connectButton.setOnClickListener {
//            val serverPort = serverPortEditText.text.toString()
//            if (serverPort.isNotEmpty()) {
//                Toast.makeText(this, "Connecting to server on port $serverPort", Toast.LENGTH_SHORT).show()
//                // Server connection logic could go here
//            } else {
//                Toast.makeText(this, "Please enter a server port", Toast.LENGTH_SHORT).show()
//            }
//        }
//
//        getWeatherForecastButton.setOnClickListener {
//            val clientAddress = clientAddressEditText.text.toString()
//            val clientPort = clientPortEditText.text.toString()
//            val city = cityEditText.text.toString()
//            val informationType = informationTypeSpinner.selectedItem.toString()
//
//            if (clientAddress.isNotEmpty() && clientPort.isNotEmpty() && city.isNotEmpty()) {
//                fetchWeatherForecast(clientAddress, clientPort.toInt(), city, informationType)
//            } else {
//                Toast.makeText(this, "Please fill in all client details", Toast.LENGTH_SHORT).show()
//            }
//        }
//    }
//
//    private fun fetchWeatherForecast(
//        clientAddress: String,
//        clientPort: Int,
//        city: String,
//        informationType: String
//    ) {
//        CoroutineScope(Dispatchers.IO).launch {
//            try {
//                // Connect to the server
//                val socket = Socket(clientAddress, clientPort)
//                val writer = PrintWriter(socket.getOutputStream(), true)
//                writer.println("$city;$informationType")
//
//                val response = socket.getInputStream().bufferedReader().readText()
//                socket.close()
//
//                // Update the UI with the response
//                withContext(Dispatchers.Main) {
//                    weatherForecastTextView.text = response
//                }
//            } catch (e: Exception) {
//                e.printStackTrace()
//                withContext(Dispatchers.Main) {
//                    weatherForecastTextView.text = "Error fetching weather data: ${e.message}"
//                }
//            }
//        }
//    }
//
//    private fun fetchFromOpenWeatherAPI(city: String, informationType: String): String {
//        // Your OpenWeather API Key
//        val apiKey = "e03cb32cfb5a67f069f2ef29237d878e"
//        val url = "https://api.openweathermap.org/data/2.5/weather?q=$city&appid=$apiKey"
//
//        return try {
//            val request = Request.Builder().url(url).build()
//            val response = httpClient.newCall(request).execute()
//            val responseBody = response.body?.string()
//            if (response.isSuccessful && !responseBody.isNullOrEmpty()) {
//                val jsonObject = JSONObject(responseBody)
//                when (informationType.lowercase()) {
//                    "temperature" -> jsonObject.getJSONObject("main").getDouble("temp").toString()
//                    "humidity" -> jsonObject.getJSONObject("main").getDouble("humidity").toString()
//                    "pressure" -> jsonObject.getJSONObject("main").getDouble("pressure").toString()
//                    else -> "Unknown parameter"
//                }
//            } else {
//                "Error fetching from API"
//            }
//        } catch (e: Exception) {
//            e.printStackTrace()
//            "Error: ${e.message}"
//        }
//    }
//}


package ro.pub.cs.systems.eim.test_practical_bun

import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    private lateinit var serverPortEditText: EditText
    private lateinit var connectButton: Button
    private lateinit var clientAddressEditText: EditText
    private lateinit var clientPortEditText: EditText
    private lateinit var cityEditText: EditText
    private lateinit var informationTypeSpinner: Spinner
    private lateinit var getWeatherForecastButton: Button
    private lateinit var weatherForecastTextView: TextView

    private var clientThread: ClientThread? = null
    private var serverThread: ServerThread? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        serverPortEditText = findViewById(R.id.server_port_edit_text)
        connectButton = findViewById(R.id.connect_button)
        clientAddressEditText = findViewById(R.id.client_address_edit_text)
        clientPortEditText = findViewById(R.id.client_port_edit_text)
        cityEditText = findViewById(R.id.city_edit_text)
        informationTypeSpinner = findViewById(R.id.information_type_spinner)
        getWeatherForecastButton = findViewById(R.id.get_weather_forecast_button)
        weatherForecastTextView = findViewById(R.id.weather_forecast_text_view)

        connectButton.setOnClickListener {
            val serverPort = serverPortEditText.text.toString().toIntOrNull()
            if (serverPort == null) {
                Toast.makeText(this, "[MAIN ACTIVITY] Server port should be filled!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            serverThread = ServerThread(serverPort)
            serverThread?.start()
        }

        getWeatherForecastButton.setOnClickListener {
            val clientAddress = clientAddressEditText.text.toString()
            val clientPort = clientPortEditText.text.toString()
            val city = cityEditText.text.toString()
            val informationType = informationTypeSpinner.selectedItem.toString()

            // Verify the client connection parameters
            if (clientAddress.isEmpty() || clientPort.isEmpty()) {
                Toast.makeText(
                    this,
                    "[MAIN ACTIVITY] Client connection parameters should be filled!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Verify the server is running
            if (serverThread == null || !serverThread!!.isAlive) {
                Toast.makeText(this, "[MAIN ACTIVITY] There is no server to connect to!", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Verify the city and information type
            if (city.isEmpty() || informationType.isEmpty()) {
                Toast.makeText(
                    this,
                    "[MAIN ACTIVITY] Parameters from client (city / information type) should be filled!",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Clear the weather forecast TextView
            weatherForecastTextView.text = ""

            // Start the client thread
            clientThread = ClientThread(
                clientAddress,
                clientPort.toInt(),
                city,
                informationType,
                weatherForecastTextView
            )
            clientThread?.start()
        }
    }

    override fun onDestroy() {
        Log.i(Constants.TAG, "[MAIN ACTIVITY] onDestroy() callback method has been invoked")
        // Stop the server thread to release resources
        if (serverThread != null) {
            serverThread?.stopThread()
        }
        super.onDestroy()
    }
}
