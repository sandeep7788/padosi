package com.dating

interface MyInterface {

    var d1:String

    fun set1(d1:String) {   // method with default implementation
        // body (optional)
        this.d1=d1
    }
    fun get(): String {
        return d1
    }
}