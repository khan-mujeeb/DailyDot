package com.example.dailydot.utils


interface Callback<T> {
    fun onResult(result: T?)
}