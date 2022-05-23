package com.danish.weatherappkotlin.utils

import java.math.BigDecimal
import java.math.RoundingMode

object HelperFunction {
    fun formatterDegree(temp: Double?): String {
        val tempToCelcius = temp?.minus(273.0)
        val formatDegree = tempToCelcius?.toBigDecimal()?.setScale(2, RoundingMode.CEILING)
        return "$formatDegree °C"
    }
}