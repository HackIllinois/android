import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.JsonParser
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import org.hackillinois.android.App
import org.hackillinois.android.database.entity.QRResponse
import retrofit2.HttpException

class RedeemViewModel : ViewModel() {

    private val _qrCodeLiveData = MutableLiveData<String>()
    val qrCodeLiveData: LiveData<String> = _qrCodeLiveData

    private val _errorLiveData = MutableLiveData<String>()
    val errorLiveData: LiveData<String> = _errorLiveData

    private var refreshJob: Job? = null

    private fun fetchQRCode() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val response: QRResponse = App.getAPI().getCartQRCode()
//                val extractedQRCode = extractQRString(response.QRCode)
                _qrCodeLiveData.postValue(response.QRCode)
            } catch (e: HttpException) {
                val errorMessage = extractErrorMessage(e.response()?.errorBody())
                _errorLiveData.postValue("Error: $errorMessage")
            } catch (e: Exception) {
                e.printStackTrace()
                _errorLiveData.postValue("Unexpected error: ${e.message}")
            }
        }
    }

    private fun extractQRString(qrCodeUrl: String): String {
        return qrCodeUrl.substringAfter("qr=", "Invalid QR Code")
    }

    // Extract the error message from the response body
    private fun extractErrorMessage(errorBody: ResponseBody?): String {
        return try {
            val jsonString = errorBody?.string() ?: return "Unknown error"
            val jsonObject = JsonParser.parseString(jsonString).asJsonObject
            jsonObject["message"]?.asString ?: "Unknown error"
        } catch (e: Exception) {
            "Failed to parse error response"
        }
    }

    fun startAutoRefresh() {
        if (refreshJob?.isActive == true) return // Prevent duplicate jobs

        refreshJob = viewModelScope.launch {
            while (isActive) {
                fetchQRCode()
                delay(15000) // Refresh every 15 seconds
            }
        }
    }

    fun stopAutoRefresh() {
        refreshJob?.cancel() // Stop refreshing when fragment is not visible
    }

    override fun onCleared() {
        super.onCleared()
        stopAutoRefresh()
    }
}
