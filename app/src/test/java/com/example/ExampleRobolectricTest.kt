package com.example

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import com.example.data.model.WeatherCodeMapper
import com.example.data.model.WeatherIconType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [34])
class ExampleRobolectricTest {

  @Test
  fun `read string from context`() {
    val context = ApplicationProvider.getApplicationContext<Context>()
    val appName = context.getString(R.string.app_name)
    assertEquals("SkyCast Weather", appName)
  }

  @Test
  fun `weather code mapping test`() {
    val sunnyDay = WeatherCodeMapper.map(0, isDay = true)
    assertEquals(WeatherIconType.SUNNY, sunnyDay.iconType)
    assertEquals("Sunny", sunnyDay.title)
    assertTrue(sunnyDay.isClear)

    val rainDay = WeatherCodeMapper.map(61, isDay = true)
    assertEquals(WeatherIconType.RAIN, rainDay.iconType)
    assertTrue(rainDay.isRaining)

    val thunderNight = WeatherCodeMapper.map(95, isDay = false)
    assertEquals(WeatherIconType.THUNDERSTORM, thunderNight.iconType)
    assertTrue(thunderNight.isThunderstorm)
  }
}
