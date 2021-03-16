package com.testm.demosdk.network

/**
 * A callback for updating the status of network calls
 *
 * @param E A result type for the call to return
 */
interface NetworkCallback<E> {

    fun onSuccess(result: E)

    fun onFailure(error: String)

}
