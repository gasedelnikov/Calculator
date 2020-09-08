package com.gri.calculator.service

class CalculationException(_errMessage: String, _errFormulaPosition : Int, _objIndex : Int) :Exception(){
    val errMessage: String = _errMessage
    val errFormulaPosition : Int = _errFormulaPosition
    val objIndex : Int = _objIndex
}