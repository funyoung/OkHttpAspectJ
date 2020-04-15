package com.sogou.lib.network.aop;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;

@Aspect
public class NetworkAspect {
    private static final String TAG = NetworkAspect.class.getSimpleName();

    @Before("execution(* com.journaldev.netorking.**(..))")
    public void onNetworkMethodBefore(JoinPoint joinPoint) throws Throwable {
        String key = joinPoint.getSignature().toString();
        Log.d(TAG, "onNetworkMethodBefore: " + key+"\n"+joinPoint.getThis());
    }
}
