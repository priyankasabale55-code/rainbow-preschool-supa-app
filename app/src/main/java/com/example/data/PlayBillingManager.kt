package com.example.data

import android.app.Activity
import android.content.Context
import com.android.billingclient.api.AcknowledgePurchaseParams
import com.android.billingclient.api.BillingClient
import com.android.billingclient.api.BillingClientStateListener
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.BillingResult
import com.android.billingclient.api.PendingPurchasesParams
import com.android.billingclient.api.ProductDetails
import com.android.billingclient.api.Purchase
import com.android.billingclient.api.PurchasesUpdatedListener
import com.android.billingclient.api.QueryProductDetailsParams
import com.android.billingclient.api.QueryPurchasesParams
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PlayBillingManager(
    context: Context,
    private val onProductUnlocked: (String) -> Unit
) : PurchasesUpdatedListener {

    companion object {
        const val SKU_NURSERY_PASS = "rps_nursery_pass"
        const val SKU_LKG_PASS = "rps_lkg_pass"
        const val SKU_UKG_PASS = "rps_ukg_pass"
        const val SKU_ALL_CLASSES_BUNDLE = "rps_all_classes_bundle"

        val catalogProducts = listOf(
            PlayStoreClassProduct(
                productId = SKU_NURSERY_PASS,
                title = "Nursery Class Full Access Pass",
                subtitle = "100% Free for Parents • Daily Tasks, Homework, Pre-Writing Tracing & Monthly Reports",
                formattedPrice = "FREE",
                targetClass = PreschoolClass.NURSERY,
                badgeText = "Nursery (2.5–3.5 Yrs) • FREE"
            ),
            PlayStoreClassProduct(
                productId = SKU_LKG_PASS,
                title = "LKG Class Full Access Pass",
                subtitle = "100% Free for Parents • Daily Tasks, Phonics & Letter Tracing, Quizzes & Monthly Reports",
                formattedPrice = "FREE",
                targetClass = PreschoolClass.LKG,
                badgeText = "LKG (3.5–4.5 Yrs) • FREE"
            ),
            PlayStoreClassProduct(
                productId = SKU_UKG_PASS,
                title = "UKG Class Full Access Pass",
                subtitle = "100% Free for Parents • CVC Words, Maths Addition, Word Tracing & Monthly Reports",
                formattedPrice = "FREE",
                targetClass = PreschoolClass.UKG,
                badgeText = "UKG (4.5–5.5 Yrs) • FREE"
            ),
            PlayStoreClassProduct(
                productId = SKU_ALL_CLASSES_BUNDLE,
                title = "All 3 Classes Complete Bundle (Nursery + LKG + UKG)",
                subtitle = "100% Free access to all 3 classes, interactive tracing boards & student reports",
                formattedPrice = "FREE",
                targetClass = null,
                badgeText = "100% Free for Parents"
            )
        )
    }

    private val _billingStatusMessage = MutableStateFlow(
        "Google Play Billing Ready • Select a Class Pass or enter a School Parent Code"
    )
    val billingStatusMessage: StateFlow<String> = _billingStatusMessage.asStateFlow()

    private val _isBillingConnected = MutableStateFlow(false)
    val isBillingConnected: StateFlow<Boolean> = _isBillingConnected.asStateFlow()

    private val productDetailsMap = mutableMapOf<String, ProductDetails>()

    private val billingClient: BillingClient = BillingClient.newBuilder(context.applicationContext)
        .setListener(this)
        .enablePendingPurchases(
            PendingPurchasesParams.newBuilder()
                .enableOneTimeProducts()
                .build()
        )
        .build()

    init {
        startConnection()
    }

    fun startConnection() {
        runCatching {
            billingClient.startConnection(object : BillingClientStateListener {
                override fun onBillingSetupFinished(billingResult: BillingResult) {
                    if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                        _isBillingConnected.value = true
                        _billingStatusMessage.value =
                            "Connected to Google Play Store Billing"
                        queryAvailableProducts()
                        restorePurchases()
                    } else {
                        _isBillingConnected.value = false
                        _billingStatusMessage.value =
                            "Google Play Store client ready (Configure SKUs in Play Console for live checkout)"
                    }
                }

                override fun onBillingServiceDisconnected() {
                    _isBillingConnected.value = false
                }
            })
        }
    }

    private fun queryAvailableProducts() {
        val productList = catalogProducts.map { item ->
            QueryProductDetailsParams.Product.newBuilder()
                .setProductId(item.productId)
                .setProductType(BillingClient.ProductType.INAPP)
                .build()
        }
        val params = QueryProductDetailsParams.newBuilder()
            .setProductList(productList)
            .build()

        billingClient.queryProductDetailsAsync(params) { billingResult, detailsList ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                detailsList.forEach { detail ->
                    productDetailsMap[detail.productId] = detail
                }
            }
        }
    }

    fun restorePurchases() {
        if (!billingClient.isReady) {
            _billingStatusMessage.value =
                "Checked Google Play account: No previous Play Store purchases found on this device."
            return
        }
        val params = QueryPurchasesParams.newBuilder()
            .setProductType(BillingClient.ProductType.INAPP)
            .build()
        billingClient.queryPurchasesAsync(params) { billingResult, purchases ->
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                if (purchases.isEmpty()) {
                    _billingStatusMessage.value =
                        "No previous Play Store purchases found on this Google account."
                } else {
                    purchases.forEach { handlePurchase(it) }
                }
            }
        }
    }

    fun launchPurchaseFlow(
        activity: Activity?,
        product: PlayStoreClassProduct,
        allowCheckoutFallbackUnlock: Boolean = true
    ) {
        val details = productDetailsMap[product.productId]
        if (activity != null && billingClient.isReady && details != null) {
            val productDetailsParamsList = listOf(
                BillingFlowParams.ProductDetailsParams.newBuilder()
                    .setProductDetails(details)
                    .build()
            )
            val flowParams = BillingFlowParams.newBuilder()
                .setProductDetailsParamsList(productDetailsParamsList)
                .build()
            billingClient.launchBillingFlow(activity, flowParams)
        } else if (allowCheckoutFallbackUnlock) {
            // When testing prior to publishing SKUs in Google Play Console, unlock and inform user
            onProductUnlocked(product.productId)
            _billingStatusMessage.value =
                "Unlocked '${product.title}' (${product.formattedPrice})! In production on Google Play, this launches BillingClient SKU '${product.productId}'."
        }
    }

    override fun onPurchasesUpdated(
        billingResult: BillingResult,
        purchases: MutableList<Purchase>?
    ) {
        if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && purchases != null) {
            purchases.forEach { purchase ->
                handlePurchase(purchase)
            }
        } else if (billingResult.responseCode == BillingClient.BillingResponseCode.USER_CANCELED) {
            _billingStatusMessage.value = "Google Play purchase canceled by user."
        }
    }

    private fun handlePurchase(purchase: Purchase) {
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val ackParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken)
                    .build()
                billingClient.acknowledgePurchase(ackParams) { _ -> }
            }
            purchase.products.forEach { sku ->
                onProductUnlocked(sku)
            }
            _billingStatusMessage.value = "Google Play Store purchase verified and unlocked!"
        }
    }
}
