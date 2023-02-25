package com.example.journey_dp.ui.viewmodel



import androidx.lifecycle.*
import com.example.journey_dp.data.domain.DirectionsResponse
import com.example.journey_dp.data.repository.Repository
import com.example.journey_dp.utils.Errors
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch


class MapViewModel(private val repository: Repository) : ViewModel() {
    private val _message = MutableLiveData<Errors<String>>()
    val message: LiveData<Errors<String>>
        get() = _message

    val defaultLocation = LatLng( 48.14838109999999, 17.1080601)

    val defaultLocationName = "Bratislava"

    private var _loading: MutableLiveData<Boolean> = MutableLiveData(false)
    val loading: MutableLiveData<Boolean> get() = _loading

    private val _directions =  MutableLiveData<DirectionsResponse?>()
    val directions: LiveData<DirectionsResponse?> get() = _directions

    private val _iconType = MutableLiveData("driving")
    val iconType: LiveData<String> get() = _iconType

    private val _location: MutableLiveData<LatLng> = MutableLiveData()
    val location: LiveData<LatLng> get() = _location



    fun getDirections(origin: String, destination: String,
    mode: String, transit: String, key: String){
        viewModelScope.launch {
            _loading.postValue(true)
            val result = repository.getDirections(origin, destination, mode, transit, key) {
                _message.postValue(
                    Errors(it)
                )
            }
            _directions.value = result
            _loading.postValue(false)
        }
    }

    fun setIconType(iconType: String) {
        _iconType.value = iconType
    }


    fun setLocation(coordinates: LatLng) {
        _location.value = coordinates
    }

    fun show(msg: String){ _message.postValue(Errors(msg))}


}