package com.juniori.puzzle.app.util

import android.annotation.SuppressLint
import android.location.Address
import java.text.SimpleDateFormat
import java.util.*

@SuppressLint("SimpleDateFormat")
private val formatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")

fun Address.toAddressString(): String {
    val list= mutableListOf<String>()
    if(adminArea !=null) list.add(adminArea)
    if(locality != null){
        list.add(locality)
    }
    else{
        list.add(subLocality)
    }
    if(thoroughfare!=null){
        list.add(thoroughfare)
    }

    return list.joinToString(" ")
}

fun String.toDate(): Date = formatter.parse(this) ?: Date()

fun String.toLocationKeyword(): List<String> {
    val result = mutableListOf<String>()
    for (len in 1..length) {
        for (index in 0..length - len) {
            result.add(substring(index, index + len))
        }
    }
    return result
}