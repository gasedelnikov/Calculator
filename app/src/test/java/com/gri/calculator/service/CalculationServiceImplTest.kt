package com.gri.calculator.service

import com.gri.calculator.service.impl.CalculationServiceImpl
import org.junit.Test

import org.junit.Assert.assertNull
import org.junit.Assert.assertEquals
import kotlin.math.pow

class CalculationServiceImplTest {
    private val assertDelta = 1.0e-12

    @Test
    fun calculationService_calculateNoContext_correct() {
        val calc = CalculationServiceImpl();

        assertEquals(11, calc.calculate(null, "11").toInt())
        assertEquals(11 * 11, calc.calculate(null, "11*11").toInt())
        assertEquals(11.0.pow(2.0), calc.calculate(null, "pow(11,2)").toDouble(), assertDelta)
    }

    @Test
    fun calculationService_calculateWithContext_correct() {
        val calc = CalculationServiceImpl();

        val contextMap: LinkedHashMap<String, Any> = LinkedHashMap();
        contextMap["v1"] = 11
        contextMap["v2"] = "v1*v1"
        contextMap["v3"] = "v2*v1"
        contextMap["v4"] = 2

        assertEquals(11, calc.calculate(contextMap, "v1").toInt())
        assertEquals(11 * 11, calc.calculate(contextMap, "v2").toInt())
        assertEquals(11 * 11 * 11, calc.calculate(contextMap, "v3").toInt())

        assertEquals(11 * 11, calc.calculate(contextMap, "v1*v1").toInt())

        assertEquals(
            11.0.pow(2.0),
            calc.calculate(contextMap, "pow(v1,v4)").toDouble(),
            assertDelta
        )
    }

    @Test
    fun calculationService_calculateNoContext_exception() {
        val calc = CalculationServiceImpl();

        try {
            calc.calculate(null, "y")
        } catch (ex: CalculationException) {
            assertEquals("undefined variable y", ex.errMessage)
            assertEquals(0, ex.errIndex)
        }

        val formula = "1234567+y";
        try {
            calc.calculate(null, formula)
        } catch (ex: CalculationException) {
            assertEquals("undefined variable y", ex.errMessage)
            assertEquals(formula.length - 1, ex.errIndex)
        }

        try {
            calc.calculate(null, formula + "+1234")
        } catch (ex: CalculationException) {
            assertEquals("undefined variable y", ex.errMessage)
            assertEquals(formula.length - 1, ex.errIndex)
        }

    }

    @Test
    fun calculationService_calculateWithContext_exception() {
        val calc = CalculationServiceImpl();

        val contextMap: LinkedHashMap<String, Any> = LinkedHashMap();
        contextMap["v2"] = "v1*v1"
        contextMap["v1"] = 11

        try {
            val result = calc.calculate(contextMap, "v2")
            assertNull(result)
        } catch (ex: CalculationException) {
            assertEquals("undefined variable v2", ex.errMessage)
            assertEquals(0, ex.errIndex)
        }

        val formula = "1234567+v2";
        try {
            val result = calc.calculate(contextMap, formula)
            assertNull(result)
        } catch (ex: CalculationException) {
            assertEquals("undefined variable v2", ex.errMessage)
            assertEquals(formula.length - 2, ex.errIndex)
        }

        try {
            val result = calc.calculate(contextMap, formula + " + 1234")
            assertNull(result)
        } catch (ex: CalculationException) {
            assertEquals("undefined variable v2", ex.errMessage)
            assertEquals(formula.length - 2, ex.errIndex)
        }
    }
}