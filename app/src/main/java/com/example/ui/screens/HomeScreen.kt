package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Air
import androidx.compose.material.icons.rounded.Compress
import androidx.compose.material.icons.rounded.DeviceThermostat
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.Nightlight
import androidx.compose.material.icons.rounded.Opacity
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FullWeatherData
import com.example.data.model.WeatherCodeMapper
import com.example.ui.components.AtmosphereBackground
import com.example.ui.components.DailyForecastSection
import com.example.ui.components.HourlyForecastSection
import com.example.ui.components.WeatherConditionIcon
import com.example.ui.components.WeatherDetailGridCard
import com.example.ui.theme.FrostedCardBackground
import com.example.ui.theme.RainBlue
import com.example.ui.theme.SkyAmberAccent
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SunGold
import com.example.ui.theme.ThunderPurple
import com.example.ui.viewmodel.WeatherUiState
import com.example.ui.viewmodel.WeatherViewModel

@Composable
fun HomeScreen(
    viewModel: WeatherViewModel,
    uiState: WeatherUiState,
    onNavigateToLocations: () -> Unit,
    modifier: Modifier = Modifier
) {
    when (uiState) {
        is WeatherUiState.Loading -> {
            AtmosphereBackground(weatherCode = 0, isDay = true, modifier = modifier) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        CircularProgressIndicator(
                            color = Color.White,
                            modifier = Modifier
                                .size(48.dp)
                                .testTag("loading_indicator")
                        )
                        Text(
                            text = "Fetching live atmosphere data...",
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White
                        )
                    }
                }
            }
        }

        is WeatherUiState.Error -> {
            AtmosphereBackground(weatherCode = 0, isDay = true, modifier = modifier) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(16.dp),
                        modifier = Modifier
                            .clip(RoundedCornerShape(24.dp))
                            .background(FrostedCardBackground)
                            .padding(24.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Air,
                            contentDescription = null,
                            tint = SkyAmberAccent,
                            modifier = Modifier.size(48.dp)
                        )
                        Text(
                            text = "Connection Notice",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = uiState.message,
                            style = MaterialTheme.typography.bodyMedium,
                            color = Color.White.copy(alpha = 0.8f),
                            textAlign = TextAlign.Center
                        )
                        ElevatedButton(
                            onClick = { viewModel.refreshWeather() },
                            colors = ButtonDefaults.elevatedButtonColors(
                                containerColor = SkyBluePrimary,
                                contentColor = Color(0xFF082F49)
                            ),
                            modifier = Modifier.testTag("retry_button")
                        ) {
                            Text("Retry Refresh", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }

        is WeatherUiState.Success -> {
            val data = uiState.data
            val condition = WeatherCodeMapper.map(data.weatherCode, data.isDay)

            AtmosphereBackground(
                weatherCode = data.weatherCode,
                isDay = data.isDay,
                modifier = modifier
            ) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(18.dp)
                ) {
                    item {
                        Spacer(modifier = Modifier.height(8.dp))
                        // Top Location & Quick Controls
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(Color(0x33000000))
                                    .clickable { onNavigateToLocations() }
                                    .padding(horizontal = 14.dp, vertical = 8.dp)
                                    .testTag("city_header_selector"),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Rounded.LocationOn,
                                    contentDescription = "Saved Locations",
                                    tint = SkyBluePrimary,
                                    modifier = Modifier.size(20.dp)
                                )
                                Column {
                                    Text(
                                        text = data.cityName,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold
                                        ),
                                        color = Color.White
                                    )
                                    if (data.country.isNotEmpty()) {
                                        Text(
                                            text = data.country,
                                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                                            color = Color.White.copy(alpha = 0.7f)
                                        )
                                    }
                                }
                            }

                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                // Unit Toggle (°C / °F)
                                Box(
                                    modifier = Modifier
                                        .clip(CircleShape)
                                        .background(Color(0x33000000))
                                        .clickable { viewModel.toggleTemperatureUnit() }
                                        .padding(horizontal = 12.dp, vertical = 8.dp)
                                        .testTag("unit_toggle_button")
                                ) {
                                    Text(
                                        text = viewModel.getUnitSymbol(),
                                        style = MaterialTheme.typography.labelLarge.copy(
                                            fontWeight = FontWeight.ExtraBold
                                        ),
                                        color = SkyAmberAccent
                                    )
                                }

                                // Refresh Button
                                IconButton(
                                    onClick = { viewModel.refreshWeather() },
                                    modifier = Modifier
                                        .size(40.dp)
                                        .clip(CircleShape)
                                        .background(Color(0x33000000))
                                        .testTag("refresh_weather_button")
                                ) {
                                    Icon(
                                        imageVector = Icons.Rounded.Refresh,
                                        contentDescription = "Refresh",
                                        tint = Color.White,
                                        modifier = Modifier.size(20.dp)
                                    )
                                }
                            }
                        }
                    }

                    item {
                        // Hero Weather Display
                        HeroWeatherSection(
                            data = data,
                            conditionTitle = condition.title,
                            conditionDesc = condition.description,
                            convertTemp = { viewModel.convertTemp(it) },
                            unitSymbol = viewModel.getUnitSymbol()
                        )
                    }

                    item {
                        // Hourly Forecast (24h)
                        HourlyForecastSection(
                            items = data.hourlyForecast,
                            convertTemp = { viewModel.convertTemp(it) },
                            unitSymbol = viewModel.getUnitSymbol()
                        )
                    }

                    item {
                        // 7-Day Outlook
                        DailyForecastSection(
                            items = data.dailyForecast,
                            convertTemp = { viewModel.convertTemp(it) },
                            unitSymbol = viewModel.getUnitSymbol()
                        )
                    }

                    item {
                        // Weather Highlights Grid
                        Text(
                            text = "ATMOSPHERIC HIGHLIGHTS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                letterSpacing = 1.sp,
                                fontWeight = FontWeight.SemiBold
                            ),
                            color = Color.White.copy(alpha = 0.8f),
                            modifier = Modifier.padding(start = 4.dp, top = 4.dp)
                        )
                    }

                    item {
                        // Highlight Cards 2x2 Grid
                        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                val (speedVal, speedUnit) = viewModel.convertSpeed(data.windSpeedKmH)
                                WeatherDetailGridCard(
                                    title = "Wind",
                                    value = "$speedVal $speedUnit",
                                    subtitle = "Direction: ${data.windDirectionDeg}°",
                                    icon = Icons.Rounded.Air,
                                    accentColor = SkyBluePrimary,
                                    modifier = Modifier.weight(1f)
                                )

                                val uvLevel = when {
                                    data.uvIndex < 3 -> "Low"
                                    data.uvIndex < 6 -> "Moderate"
                                    data.uvIndex < 8 -> "High"
                                    data.uvIndex < 11 -> "Very High"
                                    else -> "Extreme"
                                }
                                WeatherDetailGridCard(
                                    title = "UV Index",
                                    value = String.format("%.1f", data.uvIndex),
                                    subtitle = "$uvLevel Exposure",
                                    icon = Icons.Rounded.WbSunny,
                                    accentColor = SunGold,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                WeatherDetailGridCard(
                                    title = "Humidity",
                                    value = "${data.humidity}%",
                                    subtitle = if (data.humidity > 60) "High moisture" else "Comfortable",
                                    icon = Icons.Rounded.Opacity,
                                    accentColor = RainBlue,
                                    modifier = Modifier.weight(1f)
                                )

                                WeatherDetailGridCard(
                                    title = "Pressure",
                                    value = "${data.pressureHpa.toInt()} hPa",
                                    subtitle = if (data.pressureHpa > 1013) "High barometric" else "Normal",
                                    icon = Icons.Rounded.Compress,
                                    accentColor = ThunderPurple,
                                    modifier = Modifier.weight(1f)
                                )
                            }

                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                WeatherDetailGridCard(
                                    title = "Sunrise",
                                    value = data.sunriseTime,
                                    subtitle = "Dawn beginning",
                                    icon = Icons.Rounded.LightMode,
                                    accentColor = SunGold,
                                    modifier = Modifier.weight(1f)
                                )

                                WeatherDetailGridCard(
                                    title = "Sunset",
                                    value = data.sunsetTime,
                                    subtitle = "Twilight begins",
                                    icon = Icons.Rounded.Nightlight,
                                    accentColor = SkyAmberAccent,
                                    modifier = Modifier.weight(1f)
                                )
                            }
                        }
                    }

                    item {
                        Spacer(modifier = Modifier.height(28.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun HeroWeatherSection(
    data: FullWeatherData,
    conditionTitle: String,
    conditionDesc: String,
    convertTemp: (Double) -> Int,
    unitSymbol: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        val condition = WeatherCodeMapper.map(data.weatherCode, data.isDay)

        WeatherConditionIcon(
            iconType = condition.iconType,
            size = 72.dp,
            showContainer = true
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Large Temperature
        Text(
            text = "${convertTemp(data.currentTempC)}°",
            style = MaterialTheme.typography.displayLarge.copy(
                fontSize = 82.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-2).sp
            ),
            color = Color.White
        )

        // Weather Condition Title
        Text(
            text = conditionTitle,
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.SemiBold
            ),
            color = Color.White
        )

        Spacer(modifier = Modifier.height(4.dp))

        // Feels like and High / Low
        Row(
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Feels like ${convertTemp(data.feelsLikeC)}$unitSymbol",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
                color = Color.White.copy(alpha = 0.85f)
            )
            Text(
                text = "•",
                color = Color.White.copy(alpha = 0.5f)
            )
            Text(
                text = "H: ${convertTemp(data.maxTempC)}°  L: ${convertTemp(data.minTempC)}°",
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = conditionDesc,
            style = MaterialTheme.typography.bodySmall,
            color = Color.White.copy(alpha = 0.75f),
            textAlign = TextAlign.Center
        )
    }
}
