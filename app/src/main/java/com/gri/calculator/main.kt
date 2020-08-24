package com.gri.calculator

import android.util.Log
import org.apache.commons.jexl3.JexlBuilder
import org.apache.commons.jexl3.JexlContext
import org.apache.commons.jexl3.MapContext
import java.lang.Exception
import kotlin.reflect.KClass
import kotlin.reflect.full.functions
import org.apache.commons.jexl3.*
import kotlin.math.*
import java.lang.Math

private val tag = "MainActivity"

fun main(args: Array<String>) {
    // JexlArithemtic
    val jc: JexlContext = MapContext();jc.set("x", 11);jc.set("y", 11);

    var formula = "x*x + max(x,y)" //Math:
    val s = "max"
    formula = formula.replace("$s(", "Math:$s(")

    val c = Math::class
    val cc = c.functions;
    c.functions.stream()
        .map { kFunction -> kFunction.name }
        .distinct()
        .forEach { funcName -> formula = formula.replace("$funcName(", "Math:$funcName(") }

    val ns: Map<String, Any> = hashMapOf("Math" to Math::class.java)
    val jexl = JexlBuilder().namespaces(ns).create()
    val res: Any = jexl.createExpression("x*x + Math:max(x,y)").evaluate(jc)
    System.out.println(res);


}

