package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.SavedLocationEntity
import com.example.data.db.WeatherDatabase
import com.example.data.model.FullWeatherData
import com.example.data.model.GeocodingResultDto
import com.example.data.repository.WeatherRepository
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class TemperatureUnit {
    CELSIUS,
    FAHRENHEIT
}

sealed interface WeatherUiState {
    data object Loading : WeatherUiState
    data class Success(val data: FullWeatherData) : WeatherUiState
    data class Error(val message: String, val cachedData: FullWeatherData? = null) : WeatherUiState
}

class WeatherViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: WeatherRepository

    val savedLocations: StateFlow<List<SavedLocationEntity>>

    private val _selectedLocation = MutableStateFlow<SavedLocationEntity?>(null)
    val selectedLocation: StateFlow<SavedLocationEntity?> = _selectedLocation.asStateFlow()

    private val _weatherState = MutableStateFlow<WeatherUiState>(WeatherUiState.Loading)
    val weatherState: StateFlow<WeatherUiState> = _weatherState.asStateFlow()

    private val _temperatureUnit = MutableStateFlow(TemperatureUnit.CELSIUS)
    val temperatureUnit: StateFlow<TemperatureUnit> = _temperatureUnit.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _searchResults = MutableStateFlow<List<GeocodingResultDto>>(emptyList())
    val searchResults: StateFlow<List<GeocodingResultDto>> = _searchResults.asStateFlow()

    private val _isSearching = MutableStateFlow(false)
    val isSearching: StateFlow<Boolean> = _isSearching.asStateFlow()

    private val _isRefreshing = MutableStateFlow(false)
    val isRefreshing: StateFlow<Boolean> = _isRefreshing.asStateFlow()

    private var searchJob: Job? = null

    init {
        val database = WeatherDatabase.getDatabase(application)
        repository = WeatherRepository(database.locationDao())

        savedLocations = repository.savedLocations.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

        viewModelScope.launch {
            repository.initDefaultLocationsIfEmpty()
        }

        viewModelScope.launch {
            savedLocations.collect { locations ->
                if (_selectedLocation.value == null && locations.isNotEmpty()) {
                    selectLocation(locations.first())
                }
            }
        }
    }

    fun selectLocation(location: SavedLocationEntity) {
        _selectedLocation.value = location
        loadWeatherForLocation(location)
    }

    fun toggleTemperatureUnit() {
        _temperatureUnit.value = if (_temperatureUnit.value == TemperatureUnit.CELSIUS) {
            TemperatureUnit.FAHRENHEIT
        } else {
            TemperatureUnit.CELSIUS
        }
    }

    fun refreshWeather() {
        val current = _selectedLocation.value ?: return
        viewModelScope.launch {
            _isRefreshing.value = true
            loadWeatherForLocation(current, isRefresh = true)
            _isRefreshing.value = false
        }
    }

    private fun loadWeatherForLocation(location: SavedLocationEntity, isRefresh: Boolean = false) {
        viewModelScope.launch {
            if (!isRefresh && _weatherState.value !is WeatherUiState.Success) {
                _weatherState.value = WeatherUiState.Loading
            }
            val result = repository.fetchWeather(
                name = location.name,
                country = location.country,
                latitude = location.latitude,
                longitude = location.longitude,
                locationId = location.id
            )
            result.onSuccess { data ->
                _weatherState.value = WeatherUiState.Success(data)
            }.onFailure { error ->
                val prevData = (_weatherState.value as? WeatherUiState.Success)?.data
                _weatherState.value = WeatherUiState.Error(
                    message = error.localizedMessage ?: "Failed to load live weather. Please check connection.",
                    cachedData = prevData
                )
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        searchJob?.cancel()

        if (query.trim().length < 2) {
            _searchResults.value = emptyList()
            _isSearching.value = false
            return
        }

        searchJob = viewModelScope.launch {
            _isSearching.value = true
            delay(350) // debounce
            val results = repository.searchLocations(query)
            _searchResults.value = results
            _isSearching.value = false
        }
    }

    fun clearSearch() {
        _searchQuery.value = ""
        _searchResults.value = emptyList()
        _isSearching.value = false
    }

    fun addAndSelectLocation(item: GeocodingResultDto) {
        viewModelScope.launch {
            val saved = repository.saveLocation(item)
            clearSearch()
            selectLocation(saved)
        }
    }

    fun deleteLocation(id: Long) {
        viewModelScope.launch {
            repository.deleteLocation(id)
            if (_selectedLocation.value?.id == id) {
                val remaining = savedLocations.value.filter { it.id != id }
                if (remaining.isNotEmpty()) {
                    selectLocation(remaining.first())
                }
            }
        }
    }

    fun convertTemp(celsius: Double): Int {
        return if (_temperatureUnit.value == TemperatureUnit.CELSIUS) {
            kotlin.math.round(celsius).toInt()
        } else {
            kotlin.math.round((celsius * 9 / 5) + 32).toInt()
        }
    }

    fun getUnitSymbol(): String {
        return if (_temperatureUnit.value == TemperatureUnit.CELSIUS) "°C" else "°F"
    }

    fun convertSpeed(kmh: Double): Pair<Int, String> {
        return if (_temperatureUnit.value == TemperatureUnit.CELSIUS) {
            Pair(kotlin.math.round(kmh).toInt(), "km/h")
        } else {
            val mph = kmh * 0.621371
            Pair(kotlin.math.round(mph).toInt(), "mph")
        }
    }
}
