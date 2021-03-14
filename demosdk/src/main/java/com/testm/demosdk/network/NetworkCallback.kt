package com.testm.demosdk.network

interface NetworkCallback<E> {

    fun onSuccess(result: E)

    fun onFailure(message: String)

}
