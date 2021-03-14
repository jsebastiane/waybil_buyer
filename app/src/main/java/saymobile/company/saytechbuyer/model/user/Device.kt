package saymobile.company.saytechbuyer.model.user

import com.google.firebase.Timestamp

data class Device (
    val deviceId: String? = null,
    var signedIn: Boolean = false,
    var lastSignIn: Timestamp? = null
)