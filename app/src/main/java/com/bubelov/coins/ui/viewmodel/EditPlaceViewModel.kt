/*
 * This is free and unencumbered software released into the public domain.
 *
 * Anyone is free to copy, modify, publish, use, compile, sell, or
 * distribute this software, either in source code form or as a compiled
 * binary, for any purpose, commercial or non-commercial, and by any
 * means.
 *
 * In jurisdictions that recognize copyright laws, the author or authors
 * of this software dedicate any and all copyright interest in the
 * software to the public domain. We make this dedication for the benefit
 * of the public at large and to the detriment of our heirs and
 * successors. We intend this dedication to be an overt act of
 * relinquishment in perpetuity of all present and future rights to this
 * software under copyright law.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND,
 * EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF
 * MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT.
 * IN NO EVENT SHALL THE AUTHORS BE LIABLE FOR ANY CLAIM, DAMAGES OR
 * OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE,
 * ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR
 * OTHER DEALINGS IN THE SOFTWARE.
 *
 * For more information, please refer to <https://unlicense.org>
 */

package com.bubelov.coins.ui.viewmodel

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import com.bubelov.coins.dagger.Injector
import com.bubelov.coins.model.Place
import com.bubelov.coins.repository.Result
import com.bubelov.coins.repository.place.PlacesRepository
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.launch
import timber.log.Timber
import javax.inject.Inject

class EditPlaceViewModel(application: Application) : AndroidViewModel(application) {
    @Inject lateinit var placesRepository: PlacesRepository

    var place: Place? = null

    var pickedLocation: LatLng? = null

    val showProgress = MutableLiveData<Boolean>().apply { value = false }

    fun setup(place: Place?) {
        Injector.appComponent.inject(this)
        this.place = place

        if (place != null) {
            pickedLocation = LatLng(place.latitude, place.longitude)
        }
    }

    fun addPlace(place: Place): LiveData<Boolean> {
        val success = MutableLiveData<Boolean>()

        launch(UI) {
            showProgress.value = true
            val result = async { placesRepository.addPlace(place) }.await()

            when (result) {
                is Result.Success -> success.value = true
                is Result.Error -> {
                    Timber.e(result.e)
                    success.value = false
                    showProgress.value = false
                }
            }
        }

        return success
    }

    fun updatePlace(place: Place): LiveData<Boolean> {
        val success = MutableLiveData<Boolean>()

        launch(UI) {
            showProgress.value = true
            val result = async {
                placesRepository.updatePlace(place) }.await()

            when (result) {
                is Result.Success -> success.value = true
                is Result.Error -> {
                    Timber.e(result.e)
                    success.value = false
                    showProgress.value = false
                }
            }
        }

        return success
    }
}