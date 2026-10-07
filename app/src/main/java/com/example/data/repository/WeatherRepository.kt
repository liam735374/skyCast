package com.example.data.repository

import com.example.data.api.NetworkClient
import com.example.data.db.LocationDao
import com.example.data.db.SavedLocationEntity
import com.example.data.model.DailyForecastItem
import com.example.data.model.FullWeatherData
import com.example.data.model.GeocodingResultDto
import com.example.data.model.HourlyForecastItem
import com.example.data.model.WeatherCodeMapper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class WeatherRepository(private val locationDao: LocationDao) {

    val savedLocations: Flow<List<SavedLocationEntity>> = locationDao.getAllLocations()

    suspend fun initDefaultLocationsIfEmpty() = withContext(Dispatchers.IO) {
        if (locationDao.getCount() == 0) {
            val defaults = listOf(
                SavedLocationEntity(
                    id = 1,
                    name = "New York",
                    country = "United States",
                    admin1 = "New York",
                    latitude = 40.7128,
                    longitude = -74.0060,
                    displayOrder = 0
                ),
                SavedLocationEntity(
                    id = 2,
                    name = "London",
                    country = "United Kingdom",
                    admin1 = "England",
                    latitude = 51.5074,
                    longitude = -0.1278,
                    displayOrder = 1
                ),
                SavedLocationEntity(
                    id = 3,
                    name = "Tokyo",
                    country = "Japan",
                    admin1 = "Tokyo",
                    latitude = 35.6762,
                    longitude = 139.6503,
                    displayOrder = 2
                ),
                SavedLocationEntity(
                    id = 4,
                    name = "Paris",
                    country = "France",
                    admin1 = "Île-de-France",
                    latitude = 48.8566,
                    longitude = 2.3522,
                    displayOrder = 3
                ),
                SavedLocationEntity(
                    id = 5,
                    name = "Sydney",
                    country = "Australia",
                    admin1 = "New South Wales",
                    latitude = -33.8688,
                    longitude = 151.2093,
                    displayOrder = 4
                )
            )
            locationDao.insertLocations(defaults)
        }
    }

    suspend fun fetchWeather(
        name: String,
        country: String,
        latitude: Double,
        longitude: Double,
        locationId: Long? = null
    ): Result<FullWeatherData> = withContext(Dispatchers.IO) {
        try {
            val response = NetworkClient.forecastApi.getForecast(latitude, longitude)
            val current = response.current ?: throw IllegalStateException("Current weather unavailable")
            val hourly = response.hourly
            val daily = response.daily

            // Process hourly (next 24 hours)
            val hourlyItems = mutableListOf<HourlyForecastItem>()
            val nowIso = SimpleDateFormat("yyyy-MM-dd'T'HH:mm", Locale.US).format(Date())
            val currentHourPrefix = nowIso.substringBefore(":")

            val timeList = hourly?.time ?: emptyList()
            val tempList = hourly?.temperature ?: emptyList()
            val precipList = hourly?.precipitationProbability
            val codeList = hourly?.weatherCode ?: emptyList()
            val windList = hourly?.windSpeed

            var startIndex = 0
            for (i in timeList.indices) {
                if (timeList[i] >= currentHourPrefix) {
                    startIndex = i
                    break
                }
            }

            val endIndex = (startIndex + 24).coerceAtMost(timeList.size)
            for (i in startIndex until endIndex) {
                val rawTime = timeList.getOrNull(i) ?: continue
                val temp = tempList.getOrNull(i) ?: current.temperature
                val precip = precipList?.getOrNull(i) ?: 0
                val code = codeList.getOrNull(i) ?: current.weatherCode
                val wind = windList?.getOrNull(i) ?: current.windSpeed

                val timeLabel = formatHourLabel(rawTime, i == startIndex)
                val hourInt = parseHour(rawTime)
                val isDaytime = hourInt in 6..19

                hourlyItems.add(
                    HourlyForecastItem(
                        timeLabel = timeLabel,
                        fullTime = rawTime,
                        tempC = temp,
                        weatherCode = code,
                        precipProb = precip,
                        isDay = isDaytime,
                        windSpeed = wind
                    )
                )
            }

            // Process daily
            val dailyItems = mutableListOf<DailyForecastItem>()
            val dailyTimes = daily?.time ?: emptyList()
            val dailyCodes = daily?.weatherCode ?: emptyList()
            val dailyMax = daily?.temperatureMax ?: emptyList()
            val dailyMin = daily?.temperatureMin ?: emptyList()
            val dailyPrecip = daily?.precipitationProbabilityMax
            val dailyUv = daily?.uvIndexMax
            val dailySunrises = daily?.sunrise
            val dailySunsets = daily?.sunset

            for (i in dailyTimes.indices) {
                val rawDate = dailyTimes[i]
                val code = dailyCodes.getOrNull(i) ?: current.weatherCode
                val maxT = dailyMax.getOrNull(i) ?: current.temperature
                val minT = dailyMin.getOrNull(i) ?: (current.temperature - 5)
                val precip = dailyPrecip?.getOrNull(i) ?: 0
                val uv = dailyUv?.getOrNull(i) ?: current.uvIndex
                val sunrise = formatSunTime(dailySunrises?.getOrNull(i))
                val sunset = formatSunTime(dailySunsets?.getOrNull(i))

                val (dayLabel, dateLabel) = formatDayOfWeek(rawDate, i == 0)

                dailyItems.add(
                    DailyForecastItem(
                        dateLabel = dateLabel,
                        dayOfWeek = dayLabel,
                        minTempC = minT,
                        maxTempC = maxT,
                        weatherCode = code,
                        precipProb = precip,
                        uvIndex = uv,
                        sunrise = sunrise,
                        sunset = sunset
                    )
                )
            }

            val todayMax = dailyMax.firstOrNull() ?: current.temperature
            val todayMin = dailyMin.firstOrNull() ?: (current.temperature - 5)
            val todaySunrise = formatSunTime(dailySunrises?.firstOrNull())
            val todaySunset = formatSunTime(dailySunsets?.firstOrNull())

            val condition = WeatherCodeMapper.map(current.weatherCode, current.isDay == 1)

            // Update database cache if it was for a saved location
            if (locationId != null) {
                locationDao.updateWeatherCache(
                    id = locationId,
                    tempC = current.temperature,
                    weatherCode = current.weatherCode,
                    condition = condition.title,
                    time = System.currentTimeMillis()
                )
            }

            val fullData = FullWeatherData(
                cityName = name,
                country = country,
                latitude = latitude,
                longitude = longitude,
                currentTempC = current.temperature,
                feelsLikeC = current.apparentTemperature,
                weatherCode = current.weatherCode,
                isDay = current.isDay == 1,
                humidity = current.humidity,
                windSpeedKmH = current.windSpeed,
                windDirectionDeg = current.windDirection,
                uvIndex = current.uvIndex,
                pressureHpa = current.surfacePressure,
                precipitationMm = current.precipitation,
                maxTempC = todayMax,
                minTempC = todayMin,
                sunriseTime = todaySunrise,
                sunsetTime = todaySunset,
                hourlyForecast = hourlyItems,
                dailyForecast = dailyItems
            )
            Result.success(fullData)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun searchLocations(query: String): List<GeocodingResultDto> = withContext(Dispatchers.IO) {
        if (query.trim().length < 2) return@withContext emptyList()
        try {
            val response = NetworkClient.geocodingApi.searchLocations(name = query.trim())
            response.results ?: emptyList()
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun saveLocation(item: GeocodingResultDto): SavedLocationEntity = withContext(Dispatchers.IO) {
        val existing = locationDao.findByCoordinates(item.latitude, item.longitude)
        if (existing != null) {
            return@withContext existing
        }
        val entity = SavedLocationEntity(
            name = item.name,
            country = item.country ?: "",
            admin1 = item.admin1,
            latitude = item.latitude,
            longitude = item.longitude,
            displayOrder = 100
        )
        val id = locationDao.insertLocation(entity)
        entity.copy(id = id)
    }

    suspend fun deleteLocation(id: Long) = withContext(Dispatchers.IO) {
        locationDao.deleteById(id)
    }

    private fun parseHour(rawTime: String): Int {
        return try {
            val timePart = rawTime.substringAfter("T")
            timePart.substringBefore(":").toInt()
        } catch (e: Exception) {
            12
        }
    }

    private fun formatHourLabel(rawTime: String, isFirst: Boolean): String {
        if (isFirst) return "Now"
        return try {
            val hour = parseHour(rawTime)
            when {
                hour == 0 -> "12 AM"
                hour == 12 -> "12 PM"
                hour > 12 -> "${hour - 12} PM"
                else -> "$hour AM"
            }
        } catch (e: Exception) {
            rawTime.substringAfter("T")
        }
    }

    private fun formatSunTime(rawIso: String?): String {
        if (rawIso == null) return "--:--"
        return try {
            val timePart = rawIso.substringAfter("T")
            val hour = timePart.substringBefore(":").toInt()
            val min = timePart.substringAfter(":").take(2)
            val period = if (hour >= 12) "PM" else "AM"
            val displayHour = when {
                hour == 0 -> 12
                hour > 12 -> hour - 12
                else -> hour
            }
            "$displayHour:$min $period"
        } catch (e: Exception) {
            rawIso.substringAfter("T")
        }
    }

    private fun formatDayOfWeek(rawDate: String, isToday: Boolean): Pair<String, String> {
        if (isToday) return Pair("Today", "Now")
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.US)
            val date = sdf.parse(rawDate) ?: return Pair(rawDate, rawDate)
            val cal = Calendar.getInstance().apply { time = date }
            val dayName = SimpleDateFormat("EEE", Locale.US).format(date)
            val monthDay = SimpleDateFormat("MMM d", Locale.US).format(date)
            Pair(dayName, monthDay)
        } catch (e: Exception) {
            Pair(rawDate, rawDate)
        }
    }
}
