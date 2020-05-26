package com.sogou.lib.network.aop;

import android.util.Log;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.Signature;
import org.aspectj.lang.annotation.After;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;

@Aspect
public class TraceAspect {
    private static final String TAG = TraceAspect.class.getSimpleName();
    @Before("execution(* android.app.Activity.on**(..))")
    public void onActivityMethodBefore(JoinPoint joinPoint) throws Throwable {
        String key = joinPoint.getSignature().toString();
        Log.d(TAG, "onActivityMethodBefore: " + key+"\n"+joinPoint.getThis());
    }

//    @Before("call(* okhttp3.OkHttpClient.newCall(..))")
//    public void newCallMethodBefore(JoinPoint joinPoint) throws Throwable {
//        String key = joinPoint.getSignature().toString();
//        Log.d(TAG, "newCallMethodBefore: " + key+"\n"+joinPoint.getThis());
//    }

    /**
     * 构造函数被调用
     */
    @Pointcut("call(com.journaldev.okhttp..*.new(..))")
    public void callConstructor() {
    }

    @Pointcut("execution(* android.app.Activity.on**(..))")
    public void callActivityMethod() {
    }

    @Pointcut("call(* okhttp3.OkHttpClient.newCall(..))")
    public void callMethod() {
    }

    /**
     * 执行(构造函数被调用)JPoint之前
     *
     * @param joinPoint
     */
    @Before("callConstructor()")
    public void beforeConstructorCall(JoinPoint joinPoint) {
        Log.e(TAG, " before->" + joinPoint.getThis().toString() + "#" + joinPoint.getSignature().getName());
    }

    /**
     * 执行（构造函数被调用）JPoint之后
     *
     * @param joinPoint
     */
    @After("callConstructor()")
    public void afterConstructorCall(JoinPoint joinPoint) {
        Log.e(TAG, " after->" + joinPoint.getThis().toString() + "#" + joinPoint.getSignature().getName());
    }

    @Around("callActivityMethod() || callMethod()")
    public void getApplicationTime(ProceedingJoinPoint joinPoint) {
        Signature signature = joinPoint.getSignature();
        String name = signature.toShortString();
        long time = System.currentTimeMillis();
        try {
            joinPoint.proceed();
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        Log.i(TAG, name + " cost " + (System.currentTimeMillis() - time));
    }

}
