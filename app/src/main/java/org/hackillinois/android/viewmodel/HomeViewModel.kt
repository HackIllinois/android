package org.hackillinois.android.viewmodel

import androidx.lifecycle.*

class HomeViewModel : ViewModel() {

    private val currentTime: MutableLiveData<Long> = MutableLiveData()

    init {
        refresh()
    }

    fun refresh() {
        currentTime.value = System.currentTimeMillis()
    }
}
