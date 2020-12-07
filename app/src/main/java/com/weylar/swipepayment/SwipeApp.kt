package com.weylar.swipepayment

import android.app.Application
import com.stripe.android.PaymentConfiguration

class SwipeApp: Application() {

    override fun onCreate() {
        super.onCreate()

        PaymentConfiguration.init(
            applicationContext,
            "pk_test_51HveKIH4tK4rH5CnFEuh4UbErE7s36tNme5MEW9ybL3mXwrHKDjl9meLWd23kuBp9BLEyVSGQVaNGPzqRFSunNW100J9Rn0bjh"
        )
    }


}