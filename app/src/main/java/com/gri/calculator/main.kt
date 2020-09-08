package com.gri.calculator

import com.gri.calculator.service.impl.CalculationServiceImpl
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.MapContext

private val tag = "MainActivity"

fun main(args: Array<String>) {

    var calc: CalculationServiceImpl =
        CalculationServiceImpl();
    val res1 = calc.calculate(null, "11")
    val res2 = calc.calculate(null, "11*11")
    val res3 = calc.calculate(null, "exp(11)")



    System.out.println();
}

fun calc() {
    val jexl = JexlBuilder().create()
    val cMap: MutableMap<String, Any> = HashMap();
    cMap.put("x", "z*z")
    cMap.put("y", "z*z")
    cMap.put("z", 11)
    val jc: JexlContext = MapContext(cMap)
    cMap.forEach { (s, any) -> cMap[s] = jexl.createExpression(any.toString()).evaluate(jc) }

    var formula = "x*x"

    val res: Any = jexl.createExpression(formula).evaluate(jc)
    System.out.println(res);
}

