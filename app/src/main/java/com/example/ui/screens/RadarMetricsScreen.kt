package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.rounded.CheckCircle
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.Info
import androidx.compose.material.icons.rounded.LightMode
import androidx.compose.material.icons.rounded.Navigation
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.FullWeatherData
import com.example.ui.components.AtmosphereBackground
import com.example.ui.theme.FrostedCardBackground
import com.example.ui.theme.FrostedCardBorder
import com.example.ui.theme.SubtitleWhite
import com.example.ui.theme.RainBlue
import com.example.ui.theme.SkyAmberAccent
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SunGold
import com.example.ui.theme.ThunderPurple
import com.example.ui.viewmodel.WeatherUiState
import com.example.ui.viewmodel.WeatherViewModel

@Composable
fun RadarMetricsScreen(
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    val uiState by viewModel.weatherState.collectAsState()

    AtmosphereBackground(
        weatherCode = (uiState as? WeatherUiState.Success)?.data?.weatherCode ?: 0,
        isDay = (uiState as? WeatherUiState.Success)?.data?.isDay ?: true,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = "Atmospheric Radar",
                style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = "In-depth telemetry, wind dynamics & comfort",
                style = MaterialTheme.typography.bodySmall,
                color = SubtitleWhite
            )

            Spacer(modifier = Modifier.height(14.dp))

            when (val state = uiState) {
                is WeatherUiState.Success -> {
                    RadarContent(
                        data = state.data,
                        viewModel = viewModel
                    )
                }
                is WeatherUiState.Loading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = SkyBluePrimary)
                    }
                }
                is WeatherUiState.Error -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "Please load weather on Home to view radar metrics.",
                            color = Color.White,
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun RadarContent(
    data: FullWeatherData,
    viewModel: WeatherViewModel,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Wind Compass & Dynamics Card
            WindDynamicsCard(
                speedKmh = data.windSpeedKmH,
                directionDeg = data.windDirectionDeg,
                convertSpeed = { viewModel.convertSpeed(it) }
            )
        }

        item {
            // Precipitation Probability Breakdown
            PrecipitationOutlookCard(data = data)
        }

        item {
            // Solar Cycle Arc Card
            SolarCycleCard(
                sunrise = data.sunriseTime,
                sunset = data.sunsetTime,
                isDay = data.isDay
            )
        }

        item {
            // Daily Weather Advice & Recommendations Card
            WeatherAdvisorCard(data = data)
        }

        item {
            Spacer(modifier = Modifier.height(28.dp))
        }
    }
}

@Composable
fun WindDynamicsCard(
    speedKmh: Double,
    directionDeg: Int,
    convertSpeed: (Double) -> Pair<Int, String>,
    modifier: Modifier = Modifier
) {
    val (speedVal, unitStr) = convertSpeed(speedKmh)
    val cardinal = when (directionDeg) {
        in 23..67 -> "Northeast (NE)"
        in 68..112 -> "East (E)"
        in 113..157 -> "Southeast (SE)"
        in 158..202 -> "South (S)"
        in 203..247 -> "Southwest (SW)"
        in 248..292 -> "West (W)"
        in 293..337 -> "Northwest (NW)"
        else -> "North (N)"
    }

    val beaufortScale = when {
        speedKmh < 1 -> "Calm air"
        speedKmh < 12 -> "Light breeze"
        speedKmh < 28 -> "Moderate breeze"
        speedKmh < 49 -> "Strong wind"
        speedKmh < 74 -> "High gale"
        else -> "Storm conditions"
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("wind_dynamics_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = FrostedCardBackground),
        border = BorderStroke(1.dp, FrostedCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "WIND RADAR & DIRECTION",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = SubtitleWhite
                )
                Icon(
                    imageVector = Icons.Rounded.Air,
                    contentDescription = null,
                    tint = SkyBluePrimary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Compass Needle Graphic
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0x22000000)),
                    contentAlignment = Alignment.Center
                ) {
                    Canvas(modifier = Modifier.size(100.dp)) {
                        val center = Offset(size.width / 2, size.height / 2)
                        val radius = size.width / 2 - 8f

                        drawCircle(
                            color = Color(0x33FFFFFF),
                            radius = radius,
                            center = center,
                            style = Stroke(width = 2f)
                        )

                        // 4 Cardinal markers
                        val markerLength = 8f
                        drawLine(
                            color = SkyBluePrimary,
                            start = Offset(center.x, center.y - radius),
                            end = Offset(center.x, center.y - radius + markerLength),
                            strokeWidth = 3f
                        )
                        drawLine(
                            color = Color.White.copy(alpha = 0.5f),
                            start = Offset(center.x, center.y + radius),
                            end = Offset(center.x, center.y + radius - markerLength),
                            strokeWidth = 2f
                        )
                        drawLine(
                            color = Color.White.copy(alpha = 0.5f),
                            start = Offset(center.x + radius, center.y),
                            end = Offset(center.x + radius - markerLength, center.y),
                            strokeWidth = 2f
                        )
                        drawLine(
                            color = Color.White.copy(alpha = 0.5f),
                            start = Offset(center.x - radius, center.y),
                            end = Offset(center.x - radius + markerLength, center.y),
                            strokeWidth = 2f
                        )
                    }

                    // Rotating Arrow
                    Icon(
                        imageVector = Icons.Rounded.Navigation,
                        contentDescription = "Wind Direction",
                        tint = SkyAmberAccent,
                        modifier = Modifier
                            .size(36.dp)
                            .rotate(directionDeg.toFloat())
                    )
                }

                // Wind stats
                Column(
                    modifier = Modifier.padding(start = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = "$speedVal $unitStr",
                        style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                    Text(
                        text = "$cardinal ($directionDeg°)",
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.SemiBold),
                        color = SkyBluePrimary
                    )
                    Text(
                        text = beaufortScale,
                        style = MaterialTheme.typography.bodySmall,
                        color = SubtitleWhite
                    )
                }
            }
        }
    }
}

@Composable
fun PrecipitationOutlookCard(
    data: FullWeatherData,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("precipitation_outlook_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = FrostedCardBackground),
        border = BorderStroke(1.dp, FrostedCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "PRECIPITATION CHANCE (NEXT 12H)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = SubtitleWhite
                )
                Icon(
                    imageVector = Icons.Rounded.WaterDrop,
                    contentDescription = null,
                    tint = RainBlue,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            val next12Hours = data.hourlyForecast.take(12)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(90.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Bottom
            ) {
                next12Hours.forEach { item ->
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Bottom,
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "${item.precipProb}%",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = if (item.precipProb > 30) RainBlue else SubtitleWhite
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(
                            modifier = Modifier
                                .width(12.dp)
                                .height(((item.precipProb / 100f) * 50f + 6f).dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(
                                    if (item.precipProb > 40) RainBlue else Color(0x33FFFFFF)
                                )
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = item.timeLabel.replace(" ", ""),
                            style = MaterialTheme.typography.labelSmall.copy(fontSize = 9.sp),
                            color = SubtitleWhite
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SolarCycleCard(
    sunrise: String,
    sunset: String,
    isDay: Boolean,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("solar_cycle_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = FrostedCardBackground),
        border = BorderStroke(1.dp, FrostedCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "SUN & DAYLIGHT TRACKER",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = SubtitleWhite
                )
                Icon(
                    imageVector = Icons.Rounded.LightMode,
                    contentDescription = null,
                    tint = SunGold,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            // Horizon Arc Canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(70.dp),
                contentAlignment = Alignment.Center
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val width = size.width
                    val height = size.height

                    // Horizon line
                    drawLine(
                        color = Color(0x33FFFFFF),
                        start = Offset(20f, height - 10f),
                        end = Offset(width - 20f, height - 10f),
                        strokeWidth = 2f
                    )

                    // Solar arc curve (semicircle approximation)
                    drawArc(
                        color = SunGold.copy(alpha = 0.6f),
                        startAngle = 180f,
                        sweepAngle = 180f,
                        useCenter = false,
                        topLeft = Offset(width * 0.15f, 10f),
                        size = androidx.compose.ui.geometry.Size(width * 0.7f, (height - 20f) * 2),
                        style = Stroke(width = 3f, cap = StrokeCap.Round)
                    )

                    // Sun marker
                    val sunProgress = if (isDay) 0.5f else 0.95f
                    val markerX = width * 0.15f + (width * 0.7f * sunProgress)
                    val markerY = 12f
                    drawCircle(
                        color = SunGold,
                        radius = 8f,
                        center = Offset(markerX, markerY)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = "Sunrise",
                        style = MaterialTheme.typography.labelSmall,
                        color = SubtitleWhite
                    )
                    Text(
                        text = sunrise,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }

                Column(horizontalAlignment = Alignment.End) {
                    Text(
                        text = "Sunset",
                        style = MaterialTheme.typography.labelSmall,
                        color = SubtitleWhite
                    )
                    Text(
                        text = sunset,
                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }
        }
    }
}

@Composable
fun WeatherAdvisorCard(
    data: FullWeatherData,
    modifier: Modifier = Modifier
) {
    val recommendations = mutableListOf<Pair<String, String>>()

    if (data.uvIndex >= 6.0) {
        recommendations.add(Pair("Sun Protection", "Very high UV index. Sunscreen (SPF 30+) and sunglasses advised."))
    } else {
        recommendations.add(Pair("Moderate UV", "Pleasant solar exposure, low risk for short outings."))
    }

    if (data.precipitationMm > 0.5 || (data.hourlyForecast.firstOrNull()?.precipProb ?: 0) > 40) {
        recommendations.add(Pair("Rain Gear", "Rainfall likely today. Keep an umbrella or waterproof shell handy."))
    } else {
        recommendations.add(Pair("Dry Conditions", "No significant precipitation expected in the near term."))
    }

    if (data.windSpeedKmH > 35) {
        recommendations.add(Pair("Breezy", "Strong gusts detected. Secure loose outdoor items."))
    }

    if (data.humidity > 70) {
        recommendations.add(Pair("High Humidity", "Moist air with reduced cooling efficiency; stay hydrated."))
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("weather_advisor_card"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = FrostedCardBackground),
        border = BorderStroke(1.dp, FrostedCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DAILY RECOMMENDATIONS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = SubtitleWhite
                )
                Icon(
                    imageVector = Icons.Rounded.CheckCircle,
                    contentDescription = null,
                    tint = SkyAmberAccent,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            recommendations.forEach { (title, desc) ->
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .padding(top = 6.dp)
                            .clip(CircleShape)
                            .background(SkyBluePrimary)
                    )
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                            color = Color.White
                        )
                        Text(
                            text = desc,
                            style = MaterialTheme.typography.bodySmall,
                            color = SubtitleWhite
                        )
                    }
                }
            }
        }
    }
}
