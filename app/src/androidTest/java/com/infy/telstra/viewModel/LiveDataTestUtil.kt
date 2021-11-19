package com.infy.telstra.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import java.util.concurrent.CountDownLatch
import java.util.concurrent.TimeUnit
import java.util.concurrent.TimeoutException

fun <T> LiveData<T>.getOrAwaitValue() : T{
    var data : T? = null
    var latch = CountDownLatch(1)
    val observer = object :Observer<T>{
        override fun onChanged(t: T) {
            data = t
            this@getOrAwaitValue.removeObserver(this)
            latch.countDown()
        }

    }
    this.observeForever(observer)
    try {
        if(!latch.await(2, TimeUnit.SECONDS)){
            throw TimeoutException("Cannot Get Data")
        }
    }finally {
        this.removeObserver(observer)
    }

    return data as T
}