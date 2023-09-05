package i.library.base.utils

/**
 * Created by hc. on 2020/12/10
 * Describe:
 */
var originalCharArr = charArrayOf(
    'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm',
    'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'
)

var replacementCharArr = charArrayOf(
    'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M',
    'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z'
)

fun textGetCharCaps(char: Char?) : Char?{
    if(char == null) return null
    for(i in originalCharArr.indices){
        if(originalCharArr[i] == char){
            return replacementCharArr[i]
        }
    }
    return char
}