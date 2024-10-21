package site.shasmatic.flutter_veepoo_sdk

import com.inuker.bluetooth.library.Code
import com.veepoo.protocol.listener.base.IBleWriteResponse

/**
 * A utility class for handling write responses from the device.
 *
 * This class provides a method to handle the response code from the device after a write operation.
 */
class VPWriteResponse: IBleWriteResponse {
    override fun onResponse(code: Int) {
        if (code != Code.REQUEST_SUCCESS) {
            VPLogger.e("VPWriteResponse: $code")
        }
    }
}