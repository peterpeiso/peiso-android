package i.library.base.listener

import android.text.Editable
import android.text.TextWatcher
import android.widget.EditText

/**
 * Created by hc. on 2020/4/28
 * Describe: TextWatcher
 */
abstract class ChangeTextWatcher() : TextWatcher {

    var editView : EditText ?= null

    override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) { }

    override fun afterTextChanged(s: Editable?) { }

    override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) { }

}