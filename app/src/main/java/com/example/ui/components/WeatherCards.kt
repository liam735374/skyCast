package com.example.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.CalendarMonth
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.DailyForecastItem
import com.example.data.model.HourlyForecastItem
import com.example.data.model.WeatherCodeMapper
import com.example.ui.theme.FrostedCardBackground
import com.example.ui.theme.FrostedCardBorder
import com.example.ui.theme.RainBlue
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SubtitleWhite
import com.example.ui.theme.SunGold

@Composable
fun HourlyForecastSection(
    items: List<HourlyForecastItem>,
    convertTemp: (Double) -> Int,
    unitSymbol: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hourly_forecast_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = FrostedCardBackground),
        border = BorderStroke(1.dp, FrostedCardBorder)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.Schedule,
                    contentDescription = null,
                    tint = SubtitleWhite,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "HOURLY FORECAST (24H)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = SubtitleWhite
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(horizontal = 4.dp)
            ) {
                items(items) { item ->
                    HourlyItemCard(
                        item = item,
                        tempFormatted = "${convertTemp(item.tempC)}$unitSymbol"
                    )
                }
            }
        }
    }
}

@Composable
fun HourlyItemCard(
    item: HourlyForecastItem,
    tempFormatted: String,
    modifier: Modifier = Modifier
) {
    val condition = WeatherCodeMapper.map(item.weatherCode, item.isDay)

    Column(
        modifier = modifier
            .width(66.dp)
            .clip(RoundedCornerShape(18.dp))
            .background(Color(0x24000000))
            .border(1.dp, Color(0x1AFFFFFF), RoundedCornerShape(18.dp))
            .padding(vertical = 12.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Text(
            text = item.timeLabel,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = Color.White
        )

        WeatherConditionIcon(
            iconType = condition.iconType,
            size = 28.dp
        )

        if (item.precipProb > 15) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.WaterDrop,
                    contentDescription = null,
                    tint = RainBlue,
                    modifier = Modifier.size(11.dp)
                )
                Text(
                    text = "${item.precipProb}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    ),
                    color = RainBlue
                )
            }
        } else {
            Spacer(modifier = Modifier.height(14.dp))
        }

        Text(
            text = tempFormatted,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White
        )
    }
}

@Composable
fun DailyForecastSection(
    items: List<DailyForecastItem>,
    convertTemp: (Double) -> Int,
    unitSymbol: String,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("daily_forecast_card"),
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(containerColor = FrostedCardBackground),
        border = BorderStroke(1.dp, FrostedCardBorder)
    ) {
        Column(modifier = Modifier.padding(18.dp)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Rounded.CalendarMonth,
                    contentDescription = null,
                    tint = SubtitleWhite,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "7-DAY OUTLOOK",
                    style = MaterialTheme.typography.labelMedium.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = SubtitleWhite
                )
            }

            Spacer(modifier = Modifier.height(14.dp))

            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                items.forEach { dayItem ->
                    DailyItemRow(
                        item = dayItem,
                        minTempFormatted = "${convertTemp(dayItem.minTempC)}°",
                        maxTempFormatted = "${convertTemp(dayItem.maxTempC)}°"
                    )
                }
            }
        }
    }
}

@Composable
fun DailyItemRow(
    item: DailyForecastItem,
    minTempFormatted: String,
    maxTempFormatted: String,
    modifier: Modifier = Modifier
) {
    val condition = WeatherCodeMapper.map(item.weatherCode, isDay = true)

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Day name & date
        Column(modifier = Modifier.width(80.dp)) {
            Text(
                text = item.dayOfWeek,
                style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                color = Color.White
            )
            Text(
                text = item.dateLabel,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = SubtitleWhite
            )
        }

        // Icon & precipitation
        Row(
            modifier = Modifier.width(60.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            WeatherConditionIcon(iconType = condition.iconType, size = 24.dp)
            if (item.precipProb > 20) {
                Text(
                    text = "${item.precipProb}%",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = RainBlue
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Min temp
        Text(
            text = minTempFormatted,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Medium),
            color = SubtitleWhite,
            modifier = Modifier.width(36.dp)
        )

        // Visual gradient temperature bar
        Box(
            modifier = Modifier
                .weight(1f)
                .height(6.dp)
                .clip(CircleShape)
                .background(Color(0x33FFFFFF))
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(0.85f)
                    .height(6.dp)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(SkyBluePrimary, SunGold)
                        )
                    )
            )
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Max temp
        Text(
            text = maxTempFormatted,
            style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
            color = Color.White,
            modifier = Modifier.width(36.dp)
        )
    }
}

@Composable
fun WeatherDetailGridCard(
    title: String,
    value: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .testTag("detail_card_${title.lowercase().replace(" ", "_")}"),
        shape = RoundedCornerShape(22.dp),
        colors = CardDefaults.cardColors(containerColor = FrostedCardBackground),
        border = BorderStroke(1.dp, FrostedCardBorder)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = title.uppercase(),
                    style = MaterialTheme.typography.labelSmall.copy(
                        letterSpacing = 1.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = SubtitleWhite
                )
                Box(
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape)
                        .background(accentColor.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = accentColor,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold
                ),
                color = Color.White
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = SubtitleWhite
            )
        }
    }
}
