package com.gri.calculator.service

import com.gri.calculator.service.impl.CalculationServiceImpl
import org.junit.Test

import org.junit.Assert.assertNull
import org.junit.Assert.assertEquals
import kotlin.math.pow

class CalculationServiceImplTest {
    private val aDelta = 1.0e-12
    private val eleven = 11.0

    @Test
    fun calculationService_calculateNoContext_correct() {
        val calc = CalculationServiceImpl();

        assertEquals(eleven, calc.calculate(null, "11").toDouble(), aDelta)
        assertEquals(eleven.pow(2), calc.calculate(null, "11*11").toDouble(), aDelta)
        assertEquals(eleven.pow(2), calc.calculate(null, "pow(11,2)").toDouble(), aDelta)
    }

    @Test
    fun calculationService_calculateWithContext_correct() {
        val calc = CalculationServiceImpl();

        val context =
            arrayListOf(Pair("v1", 11), Pair("v2", "v1*v1"), Pair("v3", "v2*v1"), Pair("v4", 2))

        assertEquals(eleven, calc.calculate(context, "v1").toDouble(), aDelta)
        assertEquals(eleven.pow(2), calc.calculate(context, "v2").toDouble(), aDelta)
        assertEquals(eleven.pow(3), calc.calculate(context, "v3").toDouble(), aDelta)

        assertEquals(eleven.pow(2), calc.calculate(context, "v1*v1").toDouble(), aDelta)

        assertEquals(eleven.pow(2), calc.calculate(context, "pow(v1,v4)").toDouble(), aDelta)
    }

    @Test
    fun calculationService_calculateNoContext_exception() {
        val calc = CalculationServiceImpl();

        try {
            calc.calculate(null, "y")
        } catch (ex: CalculationException) {
            assertEquals("undefined variable y", ex.errMessage)
            assertEquals(CalculationErrorPlace.FORMULA, ex.calculationErrorPlace)
            assertEquals(0, ex.errFormulaPosition)
            assertEquals(0, ex.objIndex)
        }

        val formula = "1234567+y";
        try {
            calc.calculate(null, formula)
        } catch (ex: CalculationException) {
            assertEquals("undefined variable y", ex.errMessage)
            assertEquals(CalculationErrorPlace.FORMULA, ex.calculationErrorPlace)
            assertEquals(formula.length - 1, ex.errFormulaPosition)
            assertEquals(0, ex.objIndex)
        }

        try {
            calc.calculate(null, "$formula+1234")
        } catch (ex: CalculationException) {
            assertEquals("undefined variable y", ex.errMessage)
            assertEquals(CalculationErrorPlace.FORMULA, ex.calculationErrorPlace)
            assertEquals(formula.length - 1, ex.errFormulaPosition)
            assertEquals(0, ex.objIndex)
        }
    }

    @Test
    fun calculationService_calculateWithContext_exception() {
        val calc = CalculationServiceImpl();

        var context = arrayListOf(Pair("v2", "v1*v1"), Pair("v1", 11))

        try {
            val result = calc.calculate(context, "v2")
            assertNull(result)
        } catch (ex: CalculationException) {
            assertEquals("undefined variable v1", ex.errMessage)
            assertEquals(CalculationErrorPlace.CONTEXT, ex.calculationErrorPlace)
            assertEquals(0, ex.errFormulaPosition)
            assertEquals(0, ex.objIndex)
        }

        val formula = "1234567+v2";
        try {
            val result = calc.calculate(context, formula)
            assertNull(result)
        } catch (ex: CalculationException) {
            assertEquals("undefined variable v1", ex.errMessage)
            assertEquals(CalculationErrorPlace.CONTEXT, ex.calculationErrorPlace)
            assertEquals(0, ex.errFormulaPosition)
            assertEquals(0, ex.objIndex)
        }

        context = arrayListOf(Pair("v0", "11"), Pair("v2", "v1*v1"), Pair("v1", 11))
        try {
            val result = calc.calculate(context, "$formula + 1234")
            assertNull(result)
        } catch (ex: CalculationException) {
            assertEquals("undefined variable v1", ex.errMessage)
            assertEquals(CalculationErrorPlace.CONTEXT, ex.calculationErrorPlace)
            assertEquals(0, ex.errFormulaPosition)
            assertEquals(1, ex.objIndex)
        }
    }

    @Test
    fun calculationService_calculateVars_correct() {
        val calc = CalculationServiceImpl();

        val context = arrayListOf(Pair("v2", 11), Pair("v1", "v2*v2"), Pair("v3", "exp(v2)"))

        assertEquals(eleven, calc.calculate(context, "v2").toDouble(), aDelta)
        assertEquals(eleven.pow(2), calc.calculate(context, "v1").toDouble(), aDelta)
        assertEquals(Math.exp(11.0), calc.calculate(context, "v3").toDouble(), aDelta)
    }

    @Test
    fun calculationService_calculateVars_exception() {
        val calc = CalculationServiceImpl();

        val context = arrayListOf(Pair("v2", 11), Pair("v1", "v2*v2"), Pair("v3", "exp(v22)"))

        try {
            val result = calc.calculate(context, "v3")
            assertNull(result)
        } catch (ex: CalculationException) {
            assertEquals("undefined variable v22", ex.errMessage)
            assertEquals(CalculationErrorPlace.CONTEXT, ex.calculationErrorPlace)
            assertEquals(4, ex.errFormulaPosition)
            assertEquals(2, ex.objIndex)
        }
    }
}