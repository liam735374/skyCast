package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.AcUnit
import androidx.compose.material.icons.rounded.Cloud
import androidx.compose.material.icons.rounded.CloudQueue
import androidx.compose.material.icons.rounded.NightsStay
import androidx.compose.material.icons.rounded.Thunderstorm
import androidx.compose.material.icons.rounded.WaterDrop
import androidx.compose.material.icons.rounded.WbCloudy
import androidx.compose.material.icons.rounded.WbSunny
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.data.model.WeatherIconType
import com.example.ui.theme.RainBlue
import com.example.ui.theme.SnowWhite
import com.example.ui.theme.SunGold
import com.example.ui.theme.ThunderPurple

@Composable
fun WeatherConditionIcon(
    iconType: WeatherIconType,
    modifier: Modifier = Modifier,
    size: Dp = 32.dp,
    showContainer: Boolean = false
) {
    val (iconVector, tintColor, bgColor) = when (iconType) {
        WeatherIconType.SUNNY -> Triple(
            Icons.Rounded.WbSunny,
            SunGold,
            Color(0x33F59E0B)
        )
        WeatherIconType.CLEAR_NIGHT -> Triple(
            Icons.Rounded.NightsStay,
            Color(0xFF93C5FD),
            Color(0x333B82F6)
        )
        WeatherIconType.PARTLY_CLOUDY_DAY -> Triple(
            Icons.Rounded.WbCloudy,
            Color(0xFFBAE6FD),
            Color(0x2238BDF8)
        )
        WeatherIconType.PARTLY_CLOUDY_NIGHT -> Triple(
            Icons.Rounded.CloudQueue,
            Color(0xFF94A3B8),
            Color(0x2264748B)
        )
        WeatherIconType.CLOUDY, WeatherIconType.FOG -> Triple(
            Icons.Rounded.Cloud,
            Color(0xFFCBD5E1),
            Color(0x2294A3B8)
        )
        WeatherIconType.DRIZZLE, WeatherIconType.RAIN -> Triple(
            Icons.Rounded.WaterDrop,
            RainBlue,
            Color(0x3360A5FA)
        )
        WeatherIconType.HEAVY_RAIN -> Triple(
            Icons.Rounded.WaterDrop,
            Color(0xFF38BDF8),
            Color(0x330284C7)
        )
        WeatherIconType.SNOW -> Triple(
            Icons.Rounded.AcUnit,
            SnowWhite,
            Color(0x33E2E8F0)
        )
        WeatherIconType.THUNDERSTORM -> Triple(
            Icons.Rounded.Thunderstorm,
            ThunderPurple,
            Color(0x33A855F7)
        )
    }

    if (showContainer) {
        Box(
            modifier = modifier
                .size(size + 16.dp)
                .clip(CircleShape)
                .background(bgColor),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = iconVector,
                contentDescription = iconType.name,
                tint = tintColor,
                modifier = Modifier.size(size)
            )
        }
    } else {
        Icon(
            imageVector = iconVector,
            contentDescription = iconType.name,
            tint = tintColor,
            modifier = modifier.size(size)
        )
    }
}
