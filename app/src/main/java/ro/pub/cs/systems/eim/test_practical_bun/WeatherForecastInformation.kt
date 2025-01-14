data class WeatherForecastInformation(
    val temperature: String,
    val windSpeed: String,
    val condition: String,
    val pressure: String,
    val humidity: String
) {
    override fun toString(): String {
        return "WeatherForecastInformation(temperature='$temperature', windSpeed='$windSpeed', condition='$condition', pressure='$pressure', humidity='$humidity')"
    }
}
