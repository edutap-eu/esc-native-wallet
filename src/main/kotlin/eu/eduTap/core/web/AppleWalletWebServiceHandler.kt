package eu.eduTap.core.web

import eu.eduTap.core.card.apple.AppleWalletHandler
import eu.eduTap.core.storage.apple.ApplePassStorageHandler

class AppleWalletWebServiceHandler(
  private val walletHandler: AppleWalletHandler,
  val storageHandler: ApplePassStorageHandler,
) : PlatformSpecificWebHandler() {

  /**
   * @return true if the authentication token is valid, false otherwise.
   */
  fun ApplePassStorageHandler.RegisteredPass.checkAuthenticationToken(token: String): Boolean {
    return this.authenticationToken == token
  }

  /**
   * Register a device & pass with the web service.
   * POST https://yourpasshost.example.com/v1/devices/{deviceLibraryIdentifier}/registrations/{passTypeIdentifier}/{serialNumber}
   *
   * More info: https://developer.apple.com/documentation/walletpasses/register-a-pass-for-update-notifications
   *
   * @param deviceLibraryIdentifier A unique identifier you use to identify and authenticate the device.
   * @param passTypeIdentifier The pass type identifier of the pass to register for update notifications. This value corresponds to the value of the passTypeIdentifier key of the pass.
   * @param serialNumber The serial number of the pass to register. This value corresponds to the serialNumber key of the pass.
   * @param authenticationToken The authentication for a pass.
   * @param pushToken A push token the server uses to send update notifications for a registered pass to a device.
   * @return A [BasicHttpResponse] containing the response from the web service.
   */
  fun registerPass(
    deviceLibraryIdentifier: String,
    passTypeIdentifier: String,
    serialNumber: String,
    authenticationToken: String,
    pushToken: String,
  ): BasicHttpResponse {
    // Check if the device is already registered, if not, register it
    if (storageHandler.getRegisteredDevice(deviceLibraryIdentifier) == null) {
      storageHandler.registerOrUpdateDevice(deviceLibraryId = deviceLibraryIdentifier, pushToken = pushToken)
    }

    // Check if the pass is already registered, if not, register it
    val pass = storageHandler.getRegisteredPass(passTypeIdentifier, serialNumber) ?: run {
      storageHandler.registerPass(
        passTypeIdentifier = passTypeIdentifier,
        serialNumber = serialNumber,
        authenticationToken = authenticationToken
      )
      storageHandler.getRegisteredPass(passTypeIdentifier, serialNumber)
        ?: throw IllegalStateException("Pass not found, but it was just registered")
    }

    return when {
      // The request isn’t authorized.
      !pass.checkAuthenticationToken(authenticationToken) -> BasicHttpResponse(statusCode = 401)

      // Serial Number Already Registered for Device
      pass.deviceLibraryIds.contains(deviceLibraryIdentifier) -> BasicHttpResponse(statusCode = 200)

      else -> {
        storageHandler.registerPassToDevice(passTypeIdentifier, serialNumber, deviceLibraryIdentifier)
        BasicHttpResponse(statusCode = 201) // The registration is successful.
      }
    }
  }


  /**
   * GET https://yourpasshost.example.com/v1/devices/{deviceLibraryIdentifier}/registrations/{passTypeIdentifier}?passesUpdatedSince={previousLastUpdated}
   *
   * More info: https://developer.apple.com/documentation/walletpasses/get-the-list-of-updatable-passes
   *
   * @return On success, the call returns an object that contains the serial numbers for the matching passes.
   */
  fun listUpdatablePasses(
    deviceLibraryIdentifier: String,
    passTypeIdentifier: String,
    previousLastUpdated: String, // TODO use this
  ): BasicHttpResponse {
    fun noMatchingPasses() = BasicHttpResponse(statusCode = 204)

    storageHandler.getRegisteredDevice(deviceLibraryIdentifier) ?: return noMatchingPasses()

    val passes = storageHandler.getRegisteredPassesForDevice(deviceLibraryIdentifier)
      .filter { it.passTypeIdentifier == passTypeIdentifier }

    return BasicHttpResponse(
      statusCode = 200,
      body = mapOf(
        "serialNumbers" to passes.map { it.serialNumber },
        "lastUpdated" to previousLastUpdated,
      )
    )
  }

  /**
   * GET https://yourpasshost.example.com/v1/passes/{passTypeIdentifier}/{serialNumber}
   *
   * More info: https://developer.apple.com/documentation/walletpasses/send-an-updated-pass
   */
  fun getPass(
    passTypeIdentifier: String,
    serialNumber: String,
    authenticationToken: String,
  ): BasicHttpResponse {
    val pass = storageHandler.getRegisteredPass(
      passTypeIdentifier = passTypeIdentifier,
      serialNumber = serialNumber
    ) ?: return BasicHttpResponse(statusCode = 404)

    if (!pass.checkAuthenticationToken(authenticationToken)) return BasicHttpResponse(statusCode = 401)

    val esc = storageHandler.getStudentCard(serialNumber) ?: throw IllegalStateException("Student card not found")

    val passData = walletHandler.generateSignedPass(esc)

    return BasicHttpResponse(
      statusCode = 200,
      body = passData,
      headers = mapOf(
        "Content-Type" to "application/vnd.apple.pkpass",
        "Content-Disposition" to "attachment; filename=\"${esc.escn}.pkpass\"",
        "Content-Length" to passData.size.toString(),
      ),
    )
  }

  /**
   * DELETE https://yourpasshost.example.com/v1/devices/{deviceLibraryIdentifier}/registrations/{passTypeIdentifier}/{serialNumber}
   *
   * More info: https://developer.apple.com/documentation/walletpasses/unregister-a-pass-for-update-notifications
   */
  fun unregisterPass(
    deviceLibraryIdentifier: String,
    passTypeIdentifier: String,
    serialNumber: String,
    authenticationToken: String,
  ): BasicHttpResponse {
    val pass = storageHandler.getRegisteredPass(passTypeIdentifier, serialNumber) ?: return BasicHttpResponse(statusCode = 404)

    return when {
      !pass.checkAuthenticationToken(authenticationToken) -> BasicHttpResponse(statusCode = 401)
      !(pass.deviceLibraryIds.contains(deviceLibraryIdentifier)) -> BasicHttpResponse(statusCode = 200) // Pass was already unregistered
      else -> {
        storageHandler.unregisterPassToDevice(
          passTypeIdentifier = passTypeIdentifier,
          serialNumber = serialNumber,
          deviceLibraryId = deviceLibraryIdentifier
        )
        BasicHttpResponse(statusCode = 200)
      }
    }
  }


  private fun sendAPNNotification() {
    TODO()
  }
}