package i.library.base.view

import android.content.Context
import android.text.InputFilter
import android.text.InputFilter.LengthFilter
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import i.library.base.R
import java.util.regex.Pattern

/**
 * Created by hc. on 2020/7/7
 * Describe: 号码限制
 */
class PhoneEditText : AppCompatEditText {

    companion object{
        const val CODE_TYPE_86 = 86
        const val CODE_TYPE_61 = 61

        const val CODE_TYPE_CARD = 777777
        const val CODE_TYPE_BSB  = 777778
    }

    private var mCodeType = CODE_TYPE_61
    private var mSpaceArray =  arrayOf(-1)

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs){ initStyle(attrs) }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr){ initStyle(attrs) }

    private fun initStyle(attrs: AttributeSet?){
        context.obtainStyledAttributes(attrs, R.styleable.PhoneEditText).apply{
            mCodeType = getInteger(R.styleable.PhoneEditText_codeType,mCodeType)
        }.recycle()
        initSize()
    }

    private var spaceMark = ' '

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        super.onTextChanged(text, start, lengthBefore, lengthAfter)
        val str = text?.toString()?:return
        val pattern: Pattern = Pattern.compile("[0-9]*")
        if(!pattern.matcher(str.replace("$spaceMark","")).matches()){
            return
        }
        val sb = StringBuilder()
        var maxSize = 20
        when(mCodeType){
            CODE_TYPE_86 -> {
                maxSize = 13
                mSpaceArray = arrayOf(3,8)
            }
            CODE_TYPE_61 -> {
                maxSize = if(str.startsWith("04")){
                    mSpaceArray = arrayOf(4,8)
                    12
                }else{
                    mSpaceArray = arrayOf(3,7)
                    11
                }
            }
            CODE_TYPE_BSB -> {
                maxSize = 8
                mSpaceArray = arrayOf(2,5)
            }
            CODE_TYPE_CARD -> {
                maxSize = 19
                mSpaceArray = arrayOf(4,9,14)
            }
        }
        filters = arrayOf<InputFilter>(LengthFilter(maxSize))
        for(i in text.indices){
            var isNext = true
            for(k in mSpaceArray){
                if(i != k + 1 && str[i] == spaceMark){
                    isNext = false
                }
            }
            if(isNext){
                sb.append(str[i])
                for(k in mSpaceArray){
                    if(sb.length == k + 1){
                        if(sb[sb.length - 1] != spaceMark){
                            sb.insert(sb.length - 1,spaceMark)
                        }
                    }
                }
            }
        }
        val isQ = sb.toString() == str
        if (!isQ) {
            val start2 = if(sb.length < start){
                sb.length - 1
            }else{
                start
            }
            var index = start2 + 1
            if (sb[start2] == spaceMark) {
                if (lengthBefore == 0) {
                    index++
                } else {
                    index--
                }
            } else {
                if (lengthBefore == 1) {
                    if(start - start2 != 2){
                        index--
                    }
                }
            }
            setText(sb.toString())
            setSelection(index)
        }
    }

    fun initSize(){
        var maxSize = 99
        when(mCodeType){
            CODE_TYPE_86 -> {
                maxSize = 13
            }
            CODE_TYPE_61 -> {
                maxSize = 12
            }
            CODE_TYPE_BSB -> {
                maxSize = 8
                spaceMark = '-'
            }
            CODE_TYPE_CARD -> {
                maxSize = 19
            }
        }
        filters = arrayOf<InputFilter>(LengthFilter(maxSize))
    }
}