package com.weylar.swipepayment

import android.content.Intent
import android.os.Bundle
import androidx.annotation.Size
import androidx.appcompat.app.AppCompatActivity
import com.stripe.android.*
import com.stripe.android.model.*
import java.util.*


class MainActivity : AppCompatActivity() {

    private lateinit var paymentSession: PaymentSession
    private lateinit var stripe: Stripe


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        CustomerSession.initCustomerSession(this, ExampleEphemeralKeyProvider())
        paymentSession = PaymentSession(this, createPaymentSessionConfig())
        stripe = Stripe(
            this,
            "pk_test_51HveKIH4tK4rH5CnFEuh4UbErE7s36tNme5MEW9ybL3mXwrHKDjl9meLWd23kuBp9BLEyVSGQVaNGPzqRFSunNW100J9Rn0bjh"
        )
        setupPaymentSession()
    }

    private fun confirmPayment(clientSecret: String, paymentMethodId: String) {
        stripe.confirmPayment(
            this,
            ConfirmPaymentIntentParams.createWithPaymentMethodId(
                paymentMethodId,
                clientSecret,
                ""
            )
        )
    }

    class ExampleEphemeralKeyProvider : EphemeralKeyProvider {

        override fun createEphemeralKey(
            @Size(min = 4) apiVersion: String,
            keyUpdateListener: EphemeralKeyUpdateListener
        ) {

        }
    }


    private fun setupPaymentSession() {
        paymentSession.init(MyPaymentSessionListener())
        paymentSession.presentPaymentMethodSelection()

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            paymentSession.handlePaymentData(requestCode, resultCode, data)
        }
    }


}


class MyPaymentSessionListener : PaymentSession.PaymentSessionListener {

    override fun onPaymentSessionDataChanged(data: PaymentSessionData) {

        if (data.useGooglePay) {
            // customer intends to pay with Google Pay
        } else {
            data.paymentMethod?.let { paymentMethod ->
                // Display information about the selected payment method
            }
        }
        if (data.isPaymentReadyToCharge) {

        }
    }



    override fun onCommunicatingStateChanged(isCommunicating: Boolean) {
        if (isCommunicating) {
            // update UI to indicate that network communication is in progress
        } else {
            // update UI to indicate that network communication has completed
        }
    }

    override fun onError(errorCode: Int, errorMessage: String) {

    }

}


private fun createPaymentSessionConfig(): PaymentSessionConfig {
    return PaymentSessionConfig.Builder()

        // hide the phone field on the shipping information form
//            .setHiddenShippingInfoFields(
//                ShippingInfoWidget.CustomizableShippingField.PHONE_FIELD
//            )
//
//            // make the address line 2 field optional
//            .setOptionalShippingInfoFields(
//                ShippingInfoWidget.CustomizableShippingField.ADDRESS_LINE_TWO_FIELD
//            )

        // specify an address to pre-populate the shipping information form
        .setPrepopulatedShippingInfo(
            ShippingInformation(
                Address.Builder()
                    .setLine1("123 Market St")
                    .setCity("San Francisco")
                    .setState("CA")
                    .setPostalCode("94107")
                    .setCountry("US")
                    .build(),
                "Jenny Rosen",
                "4158675309"
            )
        )
        .setShippingInfoRequired(false)
        .setShippingMethodsRequired(false)
        .setPaymentMethodTypes(listOf(PaymentMethod.Type.Card))
        .setAllowedShippingCountryCodes(setOf("US", "CA"))

//            // specify a layout to display under the payment collection form
//            .setAddPaymentMethodFooter(R.layout.add_payment_method_footer)

        // specify the shipping information validation delegate
        .setShippingInformationValidator(AppShippingInfoValidator())
        .setShippingMethodsFactory(AppShippingMethodsFactory())
        .setShouldShowGooglePay(false)
        .build()
}

private class AppShippingInfoValidator : PaymentSessionConfig.ShippingInformationValidator {
    override fun getErrorMessage(
        shippingInformation: ShippingInformation
    ): String {
        return "A US address is required"
    }

    override fun isValid(
        shippingInformation: ShippingInformation
    ): Boolean {
        return Locale.US.country == shippingInformation.address?.country
    }
}

private class AppShippingMethodsFactory : PaymentSessionConfig.ShippingMethodsFactory {
    override fun create(shippingInformation: ShippingInformation): List<ShippingMethod> {
        return listOf(
            ShippingMethod(
                label = "UPS Ground",
                identifier = "ups-ground",
                detail = "Arrives in 3-5 days",
                amount = 0,
                currency = Currency.getInstance("usd")
            ),
            ShippingMethod(
                label = "FedEx",
                identifier = "fedex",
                detail = "Arrives tomorrow",
                amount = 599,
                currency = Currency.getInstance("usd")
            )
        )
    }
}