package i.library.base.listener;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by hc. on 2020/9/8
 * Describe: 页面状态
 */
public class ActivityLifecycleListener implements Application.ActivityLifecycleCallbacks {

    public int refCount = 0;
    private APPLifecycleListener mListener;

    public void bindingListener(APPLifecycleListener listener){
        this.mListener = listener;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        refCount++;
        if(refCount == 1 && mListener != null){
            mListener.onCustomAppStart();
        }
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        refCount--;
        if(refCount == 0 && mListener != null){
            mListener.onCustomAppStop();
        }
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }
}