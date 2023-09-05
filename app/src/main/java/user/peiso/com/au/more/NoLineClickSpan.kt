package user.peiso.com.au.more

import android.graphics.Color
import android.text.TextPaint
import android.text.style.ClickableSpan
import android.view.View

/**
 * Created by forever_zero on 2022/9/22.
 **/
class NoLineClickSpan(val click: () -> Unit): ClickableSpan() {

    override fun updateDrawState(ds: TextPaint) {
        ds.isUnderlineText = true
        ds.bgColor = Color.TRANSPARENT
    }

    override fun onClick(widget: View) {
        click.invoke()
    }

}