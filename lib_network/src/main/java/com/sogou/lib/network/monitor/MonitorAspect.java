package com.sogou.lib.network.monitor;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class MonitorAspect {
    private static final String TAG = MonitorAspect.class.getSimpleName();
//    @Before("execution(* android.app.Activity.on**(..))")
//    public void onActivityMethodBefore(JoinPoint joinPoint) throws Throwable {
//        String key = joinPoint.getSignature().toString();
//        Log.d(TAG, "onActivityMethodBefore: " + key+"\n"+joinPoint.getThis());
//    }

    @Before("call(* okhttp3.OkHttpClient.newCall(..))")
    public void newCallMethodBefore(JoinPoint joinPoint) throws Throwable {
        String key = joinPoint.getSignature().toString();
        Log.d(TAG, "newCallMethodBefore: " + key+"\n"+joinPoint.getThis());
    }
}
