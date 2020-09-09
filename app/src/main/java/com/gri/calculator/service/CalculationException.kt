package com.gri.calculator.service

class CalculationException(
    _errMessage: String,
    _calculationErrorPlace: CalculationErrorPlace,
    _objIndex: Int,
    _errFormulaPosition: Int
) : Exception() {
    val errMessage = _errMessage
    val calculationErrorPlace = _calculationErrorPlace
    val errFormulaPosition = _errFormulaPosition
    val objIndex = _objIndex
}