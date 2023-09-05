package i.library.base.view

import android.content.Context
import android.util.AttributeSet
import androidx.appcompat.widget.AppCompatEditText
import i.library.base.utils.textGetCharCaps

/**
 * Created by hc. on 2020/9/16
 * Describe: 限制首字母大写
 */
class EditCapitalizeView : AppCompatEditText{

    private var isOpenCapitalize = true

    constructor(context: Context) : super(context)

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)

    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int)
            : super(context, attrs, defStyleAttr)

    override fun onTextChanged(
        text: CharSequence?,
        start: Int,
        lengthBefore: Int,
        lengthAfter: Int
    ) {
        val str = text?.toString()
        if(str != null && str.isNotEmpty() && isOpenCapitalize){
            var lastChar = ' '
            val stringBuffer = StringBuffer()
            for(i in str.indices){
                val char = str[i]
                val replace = if(lastChar == ' '){
                    textGetCharCaps(char) ?: char
                }else{
                    char
                }
                stringBuffer.append(replace)
                lastChar = char
            }
            if(stringBuffer.toString() != str){
                setText(stringBuffer)
                setSelection(stringBuffer.length)
            }
        }else{
            super.onTextChanged(text, start, lengthBefore, lengthAfter)
        }
    }

}