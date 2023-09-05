package i.library.base.utils

import kotlinx.coroutines.*

/**
 * Created by hc. on 2020/5/29
 * Describe:
 */
class TimedTaskUtils{

    var mJob : Job ?= null

    /** 定时任务 **/
    fun timeRunNext(time : Long,back : () -> Unit){
        mJob = CoroutineScope(Dispatchers.IO).launch {
            delay(time)
            MainScope().launch {
                back.invoke()
            }
        }
    }

    /** 定时任务 **/
    fun timeRun(time : Long,back : () -> Unit){
        mJob = CoroutineScope(Dispatchers.IO).launch {
            delay(time)
            MainScope().launch {
                back.invoke()
            }
            timeRun(time,back)
        }
    }

    /** 倒计时 **/
    private var mTimeCount = 0
    fun timeCountDownRun(time : Long,count : Int,back : (count : Int) -> Unit){
        if(count == 0){
            back.invoke(0)
            return
        }
        mTimeCount = count
        timeRun(time){
            back.invoke(mTimeCount)
            if(mTimeCount == 0){
                close()
            }
            mTimeCount -= 1
        }
    }

    /** 倒计时 **/
    fun timeCountDownNowRun(time : Long,count : Int,back : (count : Int) -> Unit){
        if(count == 0){
            back.invoke(count)
            return
        }
        mTimeCount = count
        back.invoke(mTimeCount)
        timeRun(time){
            mTimeCount -= 1
            back.invoke(mTimeCount)
            if(mTimeCount == 0){
                close()
            }
        }
    }

    /** 关闭 **/
    fun close(){
        mTimeCount = 0
        mJob?.cancel()
    }
}