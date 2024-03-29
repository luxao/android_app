package com.example.journey_dp.ui.viewmodel


import android.graphics.Bitmap
import android.util.Log
import android.widget.LinearLayout
import androidx.lifecycle.*
import com.example.journey_dp.data.domain.DirectionsResponse
import com.example.journey_dp.data.domain.Place
import com.example.journey_dp.data.domain.Step
import com.example.journey_dp.data.repository.Repository
import com.example.journey_dp.utils.Errors
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.Polyline
import kotlinx.coroutines.launch


class MapViewModel(private val repository: Repository) : ViewModel() {
    private val _message = MutableLiveData<Errors<String>>()
    val message: LiveData<Errors<String>>
        get() = _message

    val defaultLocation = LatLng(48.14838109999999, 17.1080601)
    val defaultLocationName = "Bratislava"

    var inputs = mutableListOf<LinearLayout>()
    var markers = mutableListOf<Marker>()
    var poiMarkers = mutableListOf<Marker>()
    var infoMarkers = mutableListOf<Marker>()
    var polylines = mutableListOf<Polyline>()
    var travelMode = mutableListOf<String>()
    var notes = mutableListOf<String>()
    var destinationsName = mutableListOf<String>()



    var newOrigin = mutableListOf<String>()
    var placeIds = mutableListOf<String>()
    var bitmapList = mutableListOf<List<Bitmap>>()
    var addressList = mutableListOf<String>()
    var phoneList = mutableListOf<String>()
    var websiteList = mutableListOf<String>()
    var wikiInfoList = mutableListOf<String>()


    var checkLine: String = ""
    var changeUserLocation = false

    var changeBetweenWaypoints = false
    var callNewDirections = false
    var nextDestination = ""


    var stepsList = mutableListOf<List<Step>>()

    private var _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: MutableLiveData<Boolean> get() = _loading

    private var _directions = MutableLiveData<DirectionsResponse?>()
    val directions: LiveData<DirectionsResponse?> get() = _directions

    private var _iconType = MutableLiveData("driving")
    val iconType: LiveData<String> get() = _iconType

    private var _countryCode: MutableLiveData<String> = MutableLiveData()
    val countryCode: LiveData<String> get() = _countryCode

    var location = LatLng(0.0, 0.0)
    var locationName = ""
    var cityInfo = ""
    var placesTypes = listOf<Place>()


    fun getDirections(
        origin: String, destination: String,
        mode: String, transit: String, key: String
    ) {
        viewModelScope.launch {
            _loading.postValue(true)
            val result = repository.getDirections(origin, destination, mode, transit, key) {
                _message.postValue(
                    Errors(it)
                )
            }
            if (result != null) {
                if (mode == "transit") {
                    _iconType.value = transit
                }
                else {
                    _iconType.value = mode
                }
                _directions.value = result
            }
            _loading.postValue(false)
        }
    }

    fun getWikiInfo(
        title: String,
        successCallback: (String) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        repository.getWikiInfo(
            title = title,
            callback = { extract ->
                cityInfo = extract
                Log.i("MYTEST", "FROM VIEWMODEL : $cityInfo")
                successCallback(cityInfo)

            },
            errorCallback = {error ->
                Log.e("MYTEST" ,"ERROR : $error")
                errorCallback(error)
            }
        )
    }

    fun getPlacesTypes(
        location: LatLng,
        radius: Int,
        type: String,
        key: String,
        successCallback: (List<Place>) -> Unit,
        errorCallback: (String) -> Unit
    ) {
        repository.searchNearby(
            location = location,
            radius = radius,
            type = type,
            key = key,
            callback = { data ->
                placesTypes = data
                successCallback(placesTypes)
            },
            errorCallback = { error ->
                Log.e("MYTEST","ERROR : $error")
                errorCallback(error)
            }
        )
    }

    fun setDirectionsToStart() {
        _directions.value = null
    }

    fun setIconType(iconType: String) {
        _iconType.value = iconType
    }


    fun setLine(lineValue: String) {
        this.checkLine = lineValue
    }

    fun setCountry(country: String) {
        _countryCode.value = country
    }


    fun show(msg: String) {
        _message.postValue(Errors(msg))
    }


}