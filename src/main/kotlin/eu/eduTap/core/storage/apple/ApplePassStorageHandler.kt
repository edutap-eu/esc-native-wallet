package eu.eduTap.core.storage.apple

import eu.eduTap.core.storage.PlatformSpecificStorageHandler
import java.util.Date

/**
 * More info: https://developer.apple.com/documentation/walletpasses/adding-a-web-service-to-update-passes#Store-Information
 */
interface ApplePassStorageHandler : PlatformSpecificStorageHandler {
  interface RegisteredDevice {
    var deviceLibraryId: String
    var pushToken: String // server uses to send update notifications
  }

  interface RegisteredPass {
    var deviceLibraryIds: List<String> // deviceLibraryId of the registered devices
    var passTypeIdentifier: String
    var serialNumber: String
    var lastUpdatedDate: Date
    var authenticationToken: String
  }

  fun getRegisteredDevice(deviceLibraryId: String): RegisteredDevice?

  fun registerOrUpdateDevice(deviceLibraryId: String, pushToken: String)

  fun getRegisteredPass(passTypeIdentifier: String, serialNumber: String): RegisteredPass?

  fun registerPass(
    passTypeIdentifier: String,
    serialNumber: String,
    authenticationToken: String,
  )

  fun registerPassToDevice(
    passTypeIdentifier: String,
    serialNumber: String,
    deviceLibraryId: String,
  )

  fun unregisterPassToDevice(
    passTypeIdentifier: String,
    serialNumber: String? = null, // If null, unregister all passes with passTypeIdentifier for a device
    deviceLibraryId: String,
  )

  fun getRegisteredPassesForDevice(deviceLibraryId: String): List<RegisteredPass>

  fun getRegisteredDevicesForPass(passTypeIdentifier: String, serialNumber: String): List<RegisteredDevice> {
    val registeredPass = getRegisteredPass(passTypeIdentifier, serialNumber)
      ?: return emptyList() // Pass not registered --> no registered device

    return registeredPass.deviceLibraryIds.mapNotNull { deviceLibraryId -> getRegisteredDevice(deviceLibraryId) }
  }
}