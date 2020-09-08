package com.gri.calculator

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.util.TypedValue
import android.view.KeyEvent
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.gri.calculator.service.CalculationException
import com.gri.calculator.service.CalculationService
import com.gri.calculator.service.impl.CalculationServiceImpl
import org.apache.commons.jexl3.*
import kotlin.math.max
import kotlin.math.min
import kotlin.reflect.KFunction
import kotlin.reflect.full.functions
import kotlin.system.exitProcess


class MainActivity : View.OnKeyListener, View.OnClickListener, AppCompatActivity() {
    private val tag = "MainActivity"

    var txvResult: TextView? = null
    var edtFormula: EditText? = null
    var btnPlus: Button? = null
    var btnMinus: Button? = null
    var btnMultiply: Button? = null
    var btnDivision: Button? = null
    var btnCalculate: Button? = null
    var btnClear: Button? = null
    var btnDelete: ImageButton? = null
    var btnExp: Button? = null
    var btnLog: Button? = null
    var btnLog10: Button? = null
    var btnMax: Button? = null
    var btnMin: Button? = null
    var btnRandom: Button? = null
    var btnSqrt: Button? = null
    var btnPow: Button? = null
    var btnCopyToClipboard: Button? = null
    var btnPasteFromClipboard: Button? = null

    private val variableContainers: MutableMap<Int, Pair<EditText, EditText>> = HashMap();
//    private val mathFunctions: Collection<KFunction<*>> = Math::class.functions
//    private var jexlEngine: JexlEngine = createEngine()
    private var calculationService: CalculationService = CalculationServiceImpl()

//    private fun createEngine(): JexlEngine {
//        val ns: MutableMap<String, Any> = HashMap()
//        ns["Math"] = Math::class.java
//        return JexlBuilder().namespaces(ns).create()
//    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main)

        findViews()
        setOnClickListenerToButtons()
//        createEngine()
        initVariables()

        edtFormula?.setOnKeyListener(this)
        edtFormula?.requestFocus()
        txvResult?.setKeyListener(null);
    }

    private fun findViews() {
        Log.d(tag, "find Views")
        txvResult = findViewById(R.id.txvResult)
        edtFormula = findViewById(R.id.edtFormula)
        btnPlus = findViewById(R.id.btnPlus)
        btnMinus = findViewById(R.id.btnMinus)
        btnMultiply = findViewById(R.id.btnMultiply)
        btnDivision = findViewById(R.id.btnDivision)
        btnCalculate = findViewById(R.id.btnCalculate)
        btnClear = findViewById(R.id.btnClear)
        btnDelete = findViewById(R.id.btnDelete)
        btnExp = findViewById(R.id.btnExp)
        btnLog = findViewById(R.id.btnLog)
        btnLog10 = findViewById(R.id.btnLog10)
        btnMax = findViewById(R.id.btnMax)
        btnMin = findViewById(R.id.btnMin)
        btnRandom = findViewById(R.id.btnRandom)
        btnSqrt = findViewById(R.id.btnSqrt)
        btnPow = findViewById(R.id.btnPow)
        btnCopyToClipboard = findViewById(R.id.btnCopyToClipboard)
        btnPasteFromClipboard = findViewById(R.id.btnPasteFromClipboard)
    }

    private fun setOnClickListenerToButtons() {
        Log.d(tag, "try set OnClickListener To Buttons");
        btnPlus?.setOnClickListener(this)
        btnMinus?.setOnClickListener(this)
        btnMultiply?.setOnClickListener(this)
        btnDivision?.setOnClickListener(this)
        btnCalculate?.setOnClickListener(this)
        btnClear?.setOnClickListener(this)
        btnDelete?.setOnClickListener(this)
        btnExp?.setOnClickListener(this)
        btnLog?.setOnClickListener(this)
        btnLog10?.setOnClickListener(this)
        btnMax?.setOnClickListener(this)
        btnMin?.setOnClickListener(this)
        btnRandom?.setOnClickListener(this)
        btnSqrt?.setOnClickListener(this)
        btnPow?.setOnClickListener(this)
        btnCopyToClipboard?.setOnClickListener(this)
        btnPasteFromClipboard?.setOnClickListener(this)
    }

    private fun initVariables() {
        variableContainers[0] =
            Pair(findViewById(R.id.edtVarName1), findViewById(R.id.edtVarValue1))
        variableContainers[1] =
            Pair(findViewById(R.id.edtVarName2), findViewById(R.id.edtVarValue2))
        variableContainers[2] =
            Pair(findViewById(R.id.edtVarName3), findViewById(R.id.edtVarValue3))
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (hasFocus) {
            Looper.myLooper()?.let {
                Handler(it).postDelayed({
                    val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    imm.showSoftInput(edtFormula, 0)
                }, 0)
            }
        }
    }

    override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
        when (v?.id) {
            R.id.edtFormula -> {
                if (keyCode == KeyEvent.KEYCODE_ENTER) {
                    Log.d(tag, "edtFormula: Key = ENTER")
                    calculateFormula()
                }
            }
        }
        return false
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.mitExit -> closeApp()
            R.id.mitFormulaCopy -> copyToClipboard(edtFormula?.text.toString())
            R.id.mitFormulaPaste -> pasteFromClipboard()
            R.id.mitFormulaClear -> clearFormula()
            R.id.mitResultCopy -> copyToClipboard(txvResult?.text.toString())
            R.id.mitResultClear -> clearResult()
            R.id.mitClear -> clear()
        }

        Toast.makeText(this, item.getTitle(), Toast.LENGTH_SHORT).show()
        return super.onOptionsItemSelected(item)
    }

    override fun onClick(v: View?) {
        if (v != null) {
//            val button: Button = findViewById(v.id)
//            Log.d(tag, "onClick; button.text = " + button.text)
            when (v.id) {
                R.id.btnExp -> addOperation(resources.getString(R.string.expFormula), 4)
                R.id.btnLog -> addOperation(resources.getString(R.string.logFormula), 4)
                R.id.btnLog10 -> addOperation(resources.getString(R.string.log10Formula), 6)
                R.id.btnMax -> addOperation(resources.getString(R.string.maxFormula), 4)
                R.id.btnMin -> addOperation(resources.getString(R.string.minFormula), 4)
                R.id.btnRandom -> addOperation(resources.getString(R.string.randomFormula), 8)
                R.id.btnSqrt -> addOperation(resources.getString(R.string.sqrtFormula), 5)
                R.id.btnPow -> addOperation(resources.getString(R.string.powFormula), 4)
                R.id.btnPlus -> addOperation(resources.getString(R.string.plusFormula), 1)
                R.id.btnMinus -> addOperation(resources.getString(R.string.minusFormula), 1)
                R.id.btnDivision -> addOperation(resources.getString(R.string.divisionFormula), 1)
                R.id.btnMultiply -> addOperation(resources.getString(R.string.multiplyFormula), 1)
                R.id.btnClear -> clear()
                R.id.btnDelete -> delete()
                R.id.btnCalculate -> calculateFormula()
                R.id.btnCopyToClipboard -> copyToClipboard(txvResult?.text.toString())
                R.id.btnPasteFromClipboard -> pasteFromClipboard()
            }
        }
    }

    private fun pasteFromClipboard() {
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val data: ClipData? = clipboard.getPrimaryClip()
        val item = data?.getItemAt(0)

        val text = item?.text.toString() // ?: "";
        addOperation(text, text.length)
    }

    private fun copyToClipboard(text: String?) {
//        val text = txvResult?.text.toString()
        val clipboard = getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        val clip = ClipData.newPlainText("label", text ?: "")
        clipboard.setPrimaryClip(clip);
    }

    private fun clear() {
        clearFormula()
        clearResult()
    }

    private fun clearFormula() {
        edtFormula?.setText("")
    }

    private fun clearResult() {
        txvResult?.text = ""
    }

    private fun delete() {
        val selectionStart = min(edtFormula?.selectionStart ?: 0, edtFormula?.selectionEnd ?: 0)
        val selectionEnd = max(edtFormula?.selectionStart ?: 0, edtFormula?.selectionEnd ?: 0)
        var formula: String = edtFormula?.text.toString()
        if (selectionStart == selectionEnd) {
            if (selectionStart > 0) {
                formula = formula.removeRange(selectionStart - 1, selectionStart)
            }
        } else {
            val formulaLeft = formula.substring(0, selectionStart)
            val formulaRight = formula.substring(selectionEnd)
            formula = formulaLeft + formulaRight
        }
        edtFormula?.setText(formula)
        edtFormula?.setSelection(if (selectionStart > 0) selectionStart - 1 else 0)
    }

    private fun addOperation(operator: String, position: Int) {
        val selectionStart = min(edtFormula?.selectionStart ?: 0, edtFormula?.selectionEnd ?: 0)
        val selectionEnd = max(edtFormula?.selectionStart ?: 0, edtFormula?.selectionEnd ?: 0)

        var formula: String = edtFormula?.text.toString()
        val formulaLeft = formula.substring(0, selectionStart)
        val formulaRight = formula.substring(selectionEnd)

        formula = formulaLeft + operator + formulaRight
        edtFormula?.setText(formula)
        edtFormula?.setSelection(formulaLeft.length + position)
    }

    private fun calculateFormula() {
        val formula: String = edtFormula?.text.toString()

        if (formula != "") {
            try {
                val res: Any? = calculationService.calculate(getContext(), formula)
                txvResult?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 20.0F)
                txvResult?.setText(res?.toString())
            } catch (ex: CalculationException) {
                if (ex.errIndex >= 0) {
                    edtFormula?.setSelection(ex.errIndex, ex.errIndex)
                }

                txvResult?.setTextSize(TypedValue.COMPLEX_UNIT_SP, 15.0F)
                txvResult?.text = resources.getString(R.string.err) + ex.errMessage
//                Toast.makeText(this, errTextPrefix + errMessage, Toast.LENGTH_LONG).show();
            }
        }
    }

    private fun getContext(): LinkedHashMap<String, Any> {
        val context: LinkedHashMap<String, Any> = LinkedHashMap();

        variableContainers.forEach { (index, pair) ->
            run {
                val name = pair.first.text.toString()
                val value = pair.second.text.toString()
                if (name != "" && value != "") {
                        context[name] = value
                }
            }
        }

        return context
    }

    private fun closeApp() {
        this@MainActivity.finish()
        exitProcess(0)
    }

}