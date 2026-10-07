package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material.icons.outlined.WbCloudy
import androidx.compose.material.icons.rounded.Explore
import androidx.compose.material.icons.rounded.LocationOn
import androidx.compose.material.icons.rounded.WbCloudy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.LocationsScreen
import com.example.ui.screens.RadarMetricsScreen
import com.example.ui.theme.DarkSurface
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.SkyBluePrimary
import com.example.ui.theme.SubtitleWhite
import com.example.ui.viewmodel.WeatherViewModel

enum class WeatherAppScreen {
    FORECAST,
    RADAR,
    LOCATIONS
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                WeatherAppRoot()
            }
        }
    }
}

@Composable
fun WeatherAppRoot(
    viewModel: WeatherViewModel = viewModel()
) {
    var currentScreen by remember { mutableStateOf(WeatherAppScreen.FORECAST) }
    val weatherState by viewModel.weatherState.collectAsState()

    // Handle back button when on sub-screens
    BackHandler(enabled = currentScreen != WeatherAppScreen.FORECAST) {
        currentScreen = WeatherAppScreen.FORECAST
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        bottomBar = {
            NavigationBar(
                windowInsets = WindowInsets.navigationBars,
                containerColor = DarkSurface.copy(alpha = 0.95f),
                tonalElevation = 8.dp,
                modifier = Modifier.testTag("bottom_nav_bar")
            ) {
                // Forecast Tab
                val isForecastSelected = currentScreen == WeatherAppScreen.FORECAST
                NavigationBarItem(
                    selected = isForecastSelected,
                    onClick = { currentScreen = WeatherAppScreen.FORECAST },
                    icon = {
                        Icon(
                            imageVector = if (isForecastSelected) Icons.Rounded.WbCloudy else Icons.Outlined.WbCloudy,
                            contentDescription = "Forecast"
                        )
                    },
                    label = {
                        Text(
                            text = "Forecast",
                            fontWeight = if (isForecastSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF082F49),
                        selectedTextColor = SkyBluePrimary,
                        indicatorColor = SkyBluePrimary,
                        unselectedIconColor = SubtitleWhite,
                        unselectedTextColor = SubtitleWhite
                    ),
                    modifier = Modifier.testTag("nav_forecast")
                )

                // Radar Tab
                val isRadarSelected = currentScreen == WeatherAppScreen.RADAR
                NavigationBarItem(
                    selected = isRadarSelected,
                    onClick = { currentScreen = WeatherAppScreen.RADAR },
                    icon = {
                        Icon(
                            imageVector = if (isRadarSelected) Icons.Rounded.Explore else Icons.Outlined.Explore,
                            contentDescription = "Radar"
                        )
                    },
                    label = {
                        Text(
                            text = "Radar",
                            fontWeight = if (isRadarSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF082F49),
                        selectedTextColor = SkyBluePrimary,
                        indicatorColor = SkyBluePrimary,
                        unselectedIconColor = SubtitleWhite,
                        unselectedTextColor = SubtitleWhite
                    ),
                    modifier = Modifier.testTag("nav_radar")
                )

                // Locations Tab
                val isLocationsSelected = currentScreen == WeatherAppScreen.LOCATIONS
                NavigationBarItem(
                    selected = isLocationsSelected,
                    onClick = { currentScreen = WeatherAppScreen.LOCATIONS },
                    icon = {
                        Icon(
                            imageVector = if (isLocationsSelected) Icons.Rounded.LocationOn else Icons.Outlined.LocationOn,
                            contentDescription = "Locations"
                        )
                    },
                    label = {
                        Text(
                            text = "Locations",
                            fontWeight = if (isLocationsSelected) FontWeight.Bold else FontWeight.Normal
                        )
                    },
                    colors = NavigationBarItemDefaults.colors(
                        selectedIconColor = Color(0xFF082F49),
                        selectedTextColor = SkyBluePrimary,
                        indicatorColor = SkyBluePrimary,
                        unselectedIconColor = SubtitleWhite,
                        unselectedTextColor = SubtitleWhite
                    ),
                    modifier = Modifier.testTag("nav_locations")
                )
            }
        }
    ) { innerPadding ->
        AnimatedContent(
            targetState = currentScreen,
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            modifier = Modifier.padding(innerPadding),
            label = "screen_transition"
        ) { screen ->
            when (screen) {
                WeatherAppScreen.FORECAST -> HomeScreen(
                    viewModel = viewModel,
                    uiState = weatherState,
                    onNavigateToLocations = { currentScreen = WeatherAppScreen.LOCATIONS }
                )
                WeatherAppScreen.RADAR -> RadarMetricsScreen(
                    viewModel = viewModel
                )
                WeatherAppScreen.LOCATIONS -> LocationsScreen(
                    viewModel = viewModel,
                    onLocationSelected = { currentScreen = WeatherAppScreen.FORECAST }
                )
            }
        }
    }
}
