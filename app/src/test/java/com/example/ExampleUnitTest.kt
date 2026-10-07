package com.example

import com.example.data.model.WeatherCodeMapper
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
  @Test
  fun addition_isCorrect() {
    assertEquals(4, 2 + 2)
  }

  @Test
  fun temperature_celsius_to_fahrenheit_conversion() {
    val celsius = 20.0
    val fahrenheit = kotlin.math.round((celsius * 9 / 5) + 32).toInt()
    assertEquals(68, fahrenheit)
  }

  @Test
  fun wmo_snow_mapping() {
    val snow = WeatherCodeMapper.map(71, isDay = true)
    assertEquals(true, snow.isSnowing)
    assertEquals("Snowfall", snow.title)
  }
}
