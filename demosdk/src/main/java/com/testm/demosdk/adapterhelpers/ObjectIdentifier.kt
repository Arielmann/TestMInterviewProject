package com.testm.demosdk.adapterhelpers

/**
 * An interface to determine for each model in the app what its unique name.
 * This is used for comparing the unique name for each model for abstracting the [DefaultDiffUtilCallback] Callback.
 */
interface ObjectIdentifier {
        fun getUniqueProperty(): String?
}