package com.gri.calculator.service.impl

import com.gri.calculator.service.CalculationException
import com.gri.calculator.service.CalculationService
import org.apache.commons.jexl3.JexlEngine
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.MapContext
import org.apache.commons.jexl3.JexlException
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions

class CalculationServiceImpl :CalculationService{
    private val tag = "CalculationService"

    private val mathFunctions: Collection<KFunction<*>> = Math::class.functions
    private val jexlEngine: JexlEngine// = createEngine()

    init {
        val ns: MutableMap<String, Any> = HashMap()
        ns["Math"] = Math::class.java
        jexlEngine = JexlBuilder().namespaces(ns).create()
    }

    private fun getElContext(mapContext: LinkedHashMap<String, Any>?): JexlContext {
        val context: LinkedHashMap<String, Any> = LinkedHashMap();

        if (mapContext != null) {
            mapContext.forEach { (name, value) ->
                run {
                    if (name != "" && value != "") {
                        try {
                            val newValue = calculate(context, value.toString())
                            context[name] = newValue.toInt()
                        }
                        catch (ex: NumberFormatException) { }
                        catch (ex: CalculationException) { }
                    }
                }
            }
        }

        return MapContext(context)
    }

    @Throws(CalculationException::class)
    override fun calculate(mapContext: LinkedHashMap<String, Any>?, formula: String): String {
        val jexlContext: JexlContext = getElContext(mapContext)

        var formulaEdt: String = formula
        var result = ""

        mathFunctions.stream()
            .map { kFunction -> kFunction.name }
            .distinct()
            .forEach { funcName ->
                formulaEdt = formulaEdt.replace("$funcName(", "Math:$funcName(")
            }

        if (formula != "") {
            try {
                result = jexlEngine.createExpression(formulaEdt)?.evaluate(jexlContext).toString();
            } catch (e: JexlException) {
                val err = e.message.toString()
//                Log.d(tag, err)
                val errIndexText = err.substringAfter("@1:").substringBefore(" ")
                val errMessage = err.substringAfter("@1:").substringAfter(" ")

//                var originFormulaErrIndex = -1
//                try {
//                    val errIndex = errIndexText.toInt()
//                    if (errIndex > 0) {
//                        originFormulaErrIndex = formula.substring(0, errIndex - 1).length
//                    }
//                } catch (ex: java.lang.NumberFormatException) {
//                }

                val originFormulaErrIndex = try {
                    formula.substring(0, errIndexText.toInt() - 1).length
                } catch (ex: java.lang.NumberFormatException) {
                    -1
                }

                throw CalculationException(
                    errMessage,
                    originFormulaErrIndex
                )
            }
        }
        return result
    }


}