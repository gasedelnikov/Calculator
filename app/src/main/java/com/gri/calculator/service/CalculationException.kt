package com.gri.calculator.service

class CalculationException(_errMessage: String, _errIndex : Int) :Exception(){
    val errMessage: String = _errMessage
    val errIndex : Int = _errIndex
}