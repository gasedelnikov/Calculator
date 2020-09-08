package com.gri.calculator.service

interface CalculationService {
    @Throws(CalculationException::class)
    fun calculate(mapContext: LinkedHashMap<String, Any>?, formula: String): String
}