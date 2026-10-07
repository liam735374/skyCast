package com.example.data.model

object WeatherCodeMapper {

    fun map(code: Int, isDay: Boolean): WeatherCondition {
        return when (code) {
            0 -> WeatherCondition(
                title = if (isDay) "Sunny" else "Clear Night",
                description = if (isDay) "Clear skies with bright sunshine" else "Clear sky and starry night",
                iconType = if (isDay) WeatherIconType.SUNNY else WeatherIconType.CLEAR_NIGHT,
                isClear = true
            )
            1 -> WeatherCondition(
                title = if (isDay) "Mainly Sunny" else "Mainly Clear",
                description = "Mostly clear with a few light clouds",
                iconType = if (isDay) WeatherIconType.SUNNY else WeatherIconType.CLEAR_NIGHT,
                isClear = true
            )
            2 -> WeatherCondition(
                title = "Partly Cloudy",
                description = "Scattered clouds with periodic sun",
                iconType = if (isDay) WeatherIconType.PARTLY_CLOUDY_DAY else WeatherIconType.PARTLY_CLOUDY_NIGHT,
                isCloudy = true
            )
            3 -> WeatherCondition(
                title = "Overcast",
                description = "Dense cloud cover across the sky",
                iconType = WeatherIconType.CLOUDY,
                isCloudy = true
            )
            45, 48 -> WeatherCondition(
                title = "Foggy",
                description = "Reduced visibility with mist and fog",
                iconType = WeatherIconType.FOG,
                isCloudy = true
            )
            51, 53, 55 -> WeatherCondition(
                title = "Light Drizzle",
                description = "Gentle light rain mist",
                iconType = WeatherIconType.DRIZZLE,
                isRaining = true
            )
            56, 57 -> WeatherCondition(
                title = "Freezing Drizzle",
                description = "Chilly freezing mist",
                iconType = WeatherIconType.DRIZZLE,
                isRaining = true,
                isSnowing = true
            )
            61, 63 -> WeatherCondition(
                title = "Rain Showers",
                description = "Steady rainfall with cooling breezes",
                iconType = WeatherIconType.RAIN,
                isRaining = true
            )
            65 -> WeatherCondition(
                title = "Heavy Rain",
                description = "Torrential downpours with heavy precipitation",
                iconType = WeatherIconType.HEAVY_RAIN,
                isRaining = true
            )
            66, 67 -> WeatherCondition(
                title = "Freezing Rain",
                description = "Icy cold rain with slick surfaces",
                iconType = WeatherIconType.RAIN,
                isRaining = true
            )
            71, 73, 75, 77 -> WeatherCondition(
                title = "Snowfall",
                description = "Graceful snowflakes covering the landscape",
                iconType = WeatherIconType.SNOW,
                isSnowing = true
            )
            80, 81, 82 -> WeatherCondition(
                title = "Rain Showers",
                description = "Passing moderate rain showers",
                iconType = WeatherIconType.RAIN,
                isRaining = true
            )
            85, 86 -> WeatherCondition(
                title = "Snow Showers",
                description = "Scattered bursts of snowfall",
                iconType = WeatherIconType.SNOW,
                isSnowing = true
            )
            95 -> WeatherCondition(
                title = "Thunderstorm",
                description = "Thunder and lightning with rain bursts",
                iconType = WeatherIconType.THUNDERSTORM,
                isThunderstorm = true,
                isRaining = true
            )
            96, 99 -> WeatherCondition(
                title = "Severe Thunderstorm",
                description = "Severe thunderstorm with hail potential",
                iconType = WeatherIconType.THUNDERSTORM,
                isThunderstorm = true,
                isRaining = true
            )
            else -> WeatherCondition(
                title = "Partly Cloudy",
                description = "Pleasant conditions with gentle clouds",
                iconType = if (isDay) WeatherIconType.PARTLY_CLOUDY_DAY else WeatherIconType.PARTLY_CLOUDY_NIGHT,
                isCloudy = true
            )
        }
    }
}
