package com.gri.calculator.service.impl

import com.gri.calculator.service.CalculationErrorPlace
import com.gri.calculator.service.CalculationException
import com.gri.calculator.service.CalculationService
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.MapContext
import org.apache.commons.jexl3.JexlException
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions

class CalculationServiceImpl : CalculationService {
    private val tag = "CalculationService"

    private val mathFunctions: Collection<KFunction<*>> = Math::class.functions
    private val jexlEngine: JexlEngine// = createEngine()

    init {
        val ns: MutableMap<String, Any> = HashMap()
        ns["Math"] = Math::class.java
        jexlEngine = JexlBuilder().namespaces(ns).create()
    }

    private fun getElContext(context: List<Pair<String, Any>>?): JexlContext {
        val resultContext: MutableMap<String, Any> = HashMap();

        if (context != null) {
            for (i in context.indices) {
                val name = context[i].first
                val value = context[i].second
                if (name != "" && value != "") {
                    val newValue = calculate(MapContext(resultContext), value.toString(), CalculationErrorPlace.CONTEXT, i)
                    try {
                        resultContext[name] = newValue.toDouble()
                    } catch (ex: NumberFormatException) {
                        resultContext[name] = newValue
                    }
                }
            }
        }
        return MapContext(resultContext)
    }

    @Throws(CalculationException::class)
    override fun calculate(mapContext: List<Pair<String, Any>>?, formula: String): String {
        val jexlContext: JexlContext = getElContext(mapContext)
        return calculate(jexlContext, formula, CalculationErrorPlace.FORMULA, 0)
    }

    private fun prepareFormula(formula: String): String {
        var formulaEdt: String = formula

        mathFunctions.stream()
            .map { kFunction -> kFunction.name }
            .distinct()
            .forEach { funcName ->
                formulaEdt = formulaEdt.replace("$funcName(", "Math:$funcName(")
            }

        return formulaEdt;
    }

    @Throws(CalculationException::class)
    private fun calculate(
        jexlContext: JexlContext?,
        formula: String,
        errorPlace: CalculationErrorPlace,
        objIndex: Int
    ): String {
        var result = ""

        if (formula != "") {
            val preparedFormula = prepareFormula(formula)
            try {
                result =
                    jexlEngine.createExpression(preparedFormula)?.evaluate(jexlContext).toString()
            } catch (e: JexlException) {
                val err = e.message.toString()
//                Log.d(tag, err)
                val errIndexText = err.substringAfter("@1:").substringBefore(" ")
                val errMessage = err.substringAfter("@1:").substringAfter(" ")

                val errIndex = try {
                    errIndexText.toInt()
                } catch (ex: java.lang.NumberFormatException) {
                    -1
                }
                val formulaSubstring = preparedFormula.substring(0, errIndex - 1)
                val originFormulaErrIndex = formulaSubstring.replace("Math:", "").length
                throw CalculationException(errMessage, errorPlace, objIndex, originFormulaErrIndex)
            }
        }
        return result
    }

}