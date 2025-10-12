package dev.benj.timelinebuilder.tlevent

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.toList


/**
 * Manages event operations.
 */
class TLEventViewModel(
    private val tlEventRepository: TLEventRepository,
): ViewModel() {

    private val _eventList = MutableStateFlow<List<TLEvent>>(mutableListOf<TLEvent>())

    val eventList: StateFlow<List<TLEvent>> = _eventList.asStateFlow()

    init {
        loadEvents()
        if(_eventList.value.isEmpty()){
            tlEventRepository.loadTestData()
            loadEvents()
        }
    }

    private fun loadEvents() {
        viewModelScope.launch {
            val events = tlEventRepository.getAllEvents()
            _eventList.value = events.toList()
        }
        Logger.i("TL ViewModel - Loading Events")
    }

    fun addEvent(newEvent: TLEvent){
        viewModelScope.launch {
            tlEventRepository.addEvent(newEvent)
            Logger.i("TL ViewModel - Adding Event - $newEvent")
            loadEvents()
        }
    }

    fun editEvent(editedEvent: TLEvent){
        tlEventRepository.editEvent(editedEvent)
        loadEvents()
    }

    fun deleteEvent(id: Long){
        viewModelScope.launch {
        Logger.i("TL ViewModel - Deleting Event with ID $id")
            tlEventRepository.deleteEvent(id)
            loadEvents()
        }
    }

    fun linkEvent(id1: Long, id2: Long){
        viewModelScope.launch {
            // TODO: Implement when repository method is available
        }
    }

    fun linkEntity(){
        viewModelScope.launch {
            // TODO: Implement when repository method is available
        }
    }

}