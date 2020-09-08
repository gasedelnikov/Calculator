package com.gri.calculator.service

interface CalculationService {
    @Throws(CalculationException::class)
    fun calculate(mapContext: List<Pair<String, Any>>?, formula: String): String
}