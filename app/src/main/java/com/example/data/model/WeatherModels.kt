package com.example.data.model

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class ForecastResponse(
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "timezone") val timezone: String?,
    @Json(name = "current") val current: CurrentWeatherDto?,
    @Json(name = "hourly") val hourly: HourlyWeatherDto?,
    @Json(name = "daily") val daily: DailyWeatherDto?
)

@JsonClass(generateAdapter = true)
data class CurrentWeatherDto(
    @Json(name = "time") val time: String = "",
    @Json(name = "temperature_2m") val temperature: Double = 0.0,
    @Json(name = "relative_humidity_2m") val humidity: Int = 0,
    @Json(name = "apparent_temperature") val apparentTemperature: Double = 0.0,
    @Json(name = "is_day") val isDay: Int = 1,
    @Json(name = "precipitation") val precipitation: Double = 0.0,
    @Json(name = "weather_code") val weatherCode: Int = 0,
    @Json(name = "surface_pressure") val surfacePressure: Double = 1013.0,
    @Json(name = "wind_speed_10m") val windSpeed: Double = 0.0,
    @Json(name = "wind_direction_10m") val windDirection: Int = 0,
    @Json(name = "uv_index") val uvIndex: Double = 0.0
)

@JsonClass(generateAdapter = true)
data class HourlyWeatherDto(
    @Json(name = "time") val time: List<String> = emptyList(),
    @Json(name = "temperature_2m") val temperature: List<Double> = emptyList(),
    @Json(name = "precipitation_probability") val precipitationProbability: List<Int>? = null,
    @Json(name = "weather_code") val weatherCode: List<Int> = emptyList(),
    @Json(name = "wind_speed_10m") val windSpeed: List<Double>? = null,
    @Json(name = "relative_humidity_2m") val humidity: List<Int>? = null
)

@JsonClass(generateAdapter = true)
data class DailyWeatherDto(
    @Json(name = "time") val time: List<String> = emptyList(),
    @Json(name = "weather_code") val weatherCode: List<Int> = emptyList(),
    @Json(name = "temperature_2m_max") val temperatureMax: List<Double> = emptyList(),
    @Json(name = "temperature_2m_min") val temperatureMin: List<Double> = emptyList(),
    @Json(name = "sunrise") val sunrise: List<String>? = null,
    @Json(name = "sunset") val sunset: List<String>? = null,
    @Json(name = "precipitation_sum") val precipitationSum: List<Double>? = null,
    @Json(name = "precipitation_probability_max") val precipitationProbabilityMax: List<Int>? = null,
    @Json(name = "uv_index_max") val uvIndexMax: List<Double>? = null
)

@JsonClass(generateAdapter = true)
data class GeocodingResponse(
    @Json(name = "results") val results: List<GeocodingResultDto>? = null
)

@JsonClass(generateAdapter = true)
data class GeocodingResultDto(
    @Json(name = "id") val id: Long,
    @Json(name = "name") val name: String,
    @Json(name = "latitude") val latitude: Double,
    @Json(name = "longitude") val longitude: Double,
    @Json(name = "country") val country: String? = null,
    @Json(name = "country_code") val countryCode: String? = null,
    @Json(name = "admin1") val admin1: String? = null,
    @Json(name = "timezone") val timezone: String? = null
)

// UI and Domain Representations
data class WeatherCondition(
    val title: String,
    val description: String,
    val iconType: WeatherIconType,
    val isRaining: Boolean = false,
    val isSnowing: Boolean = false,
    val isCloudy: Boolean = false,
    val isClear: Boolean = false,
    val isThunderstorm: Boolean = false
)

enum class WeatherIconType {
    SUNNY,
    CLEAR_NIGHT,
    PARTLY_CLOUDY_DAY,
    PARTLY_CLOUDY_NIGHT,
    CLOUDY,
    FOG,
    DRIZZLE,
    RAIN,
    HEAVY_RAIN,
    SNOW,
    THUNDERSTORM
}

data class HourlyForecastItem(
    val timeLabel: String,
    val fullTime: String,
    val tempC: Double,
    val weatherCode: Int,
    val precipProb: Int,
    val isDay: Boolean,
    val windSpeed: Double
)

data class DailyForecastItem(
    val dateLabel: String,
    val dayOfWeek: String,
    val minTempC: Double,
    val maxTempC: Double,
    val weatherCode: Int,
    val precipProb: Int,
    val uvIndex: Double,
    val sunrise: String,
    val sunset: String
)

data class FullWeatherData(
    val cityName: String,
    val country: String,
    val latitude: Double,
    val longitude: Double,
    val currentTempC: Double,
    val feelsLikeC: Double,
    val weatherCode: Int,
    val isDay: Boolean,
    val humidity: Int,
    val windSpeedKmH: Double,
    val windDirectionDeg: Int,
    val uvIndex: Double,
    val pressureHpa: Double,
    val precipitationMm: Double,
    val maxTempC: Double,
    val minTempC: Double,
    val sunriseTime: String,
    val sunsetTime: String,
    val hourlyForecast: List<HourlyForecastItem>,
    val dailyForecast: List<DailyForecastItem>,
    val lastUpdated: Long = System.currentTimeMillis()
)
