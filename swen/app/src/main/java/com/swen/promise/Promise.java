package com.swen.promise;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.List;
import java.util.Vector;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;

public class Promise<IN,OUT>
{
    private static ExecutorService executorService = Executors.newCachedThreadPool();

    private Promise<?,?> parent = null;
    private Callback<IN,OUT> callback;

    private List< Promise<OUT,?> > resNext;
    private List< Promise<Throwable,?> > rejNext;

    boolean alreadyRun = false;
    OUT output;
    Throwable throwable;

    private boolean runInUI = false;
    private boolean resolved = false;
    private boolean rejected = false;

    public Promise(Callback<IN,OUT> callback)
    {
        resNext = new Vector<>();
        rejNext = new Vector<>();
        this.callback = callback;
    }

    public Promise(Callback<IN,OUT> callback, IN input)
    {
        this(callback);
        executorService.submit(() -> run(input));
    }

    private <T> Promise<OUT,T> thenPipe(Promise<OUT,T> next)
    {
        next.parent = this;
        if (!alreadyRun)
            resNext.add(next);
        else if (resolved)
            executorService.submit(() -> next.run(output));
        return next;
    }

    private <T> Promise<Throwable,T> failPipe(Promise<Throwable,T> next)
    {
        next.parent = this;
        if (!alreadyRun)
            rejNext.add(next);
        else if (rejected)
            executorService.submit(() -> next.run(throwable));
        return next;
    }

    public <T> Promise<OUT,T> then(Callback<OUT,T> callback)
    {
        return thenPipe(new Promise<OUT,T>(callback));
    }

    public <T> Promise<Throwable,T> fail(Callback<Throwable,T> callback)
    {
        return failPipe(new Promise<Throwable,T>(callback));
    }

    public <T> Promise<OUT,T> thenUI(Callback<OUT,T> callback)
    {
        Promise<OUT,T> ret = then(callback);
        ret.setRunInUI();
        return ret;
    }

    public <T> Promise<Throwable,T> failUI(Callback<Throwable,T> callback)
    {
        Promise<Throwable,T> ret = failPipe(new Promise<Throwable,T>(callback));
        ret.setRunInUI();
        return ret;
    }

    public void setRunInUI() { runInUI = true; }

    public synchronized void waitUntilHasRun() throws InterruptedException
    {
        if (alreadyRun)
            return;
        if (parent != null)
            parent.waitUntilHasRun();
        wait();
    }

    private synchronized void run(IN input)
    {
        if (runInUI)
        {
            RunMessage runMessage = new RunMessage();
            runMessage.promise = this;
            runMessage.input = input;
            Message msg = uiHandler.obtainMessage(0, runMessage);
            uiHandler.sendMessage(msg);
        } else
            runSync(input);
    }

    private static Handler uiHandler = new Handler(Looper.getMainLooper()) {
        @Override
        public void handleMessage(Message msg)
        {
            super.handleMessage(msg);
            if (msg.what == 0)
            {
                RunMessage runMessage = (RunMessage)(msg.obj);
                runMessage.promise.runSync(runMessage.input);
            }
        }
    };

    private static class RunMessage
    {
        Promise promise;
        Object input;
    }

    private synchronized void runSync(IN input)
    {
        if (alreadyRun)
            return;
        alreadyRun = true;
        try
        {
            output = callback.run(input);
            for (Promise<OUT,?> next : resNext)
                executorService.submit(() -> next.run(output));
            resolved = true;
        } catch (Throwable e)
        {
            throwable = e;
            for (Promise<Throwable,?> next : rejNext)
                executorService.submit(() -> next.run(throwable));
            rejected = true;
        }
        resNext = null;
        rejNext = null;
        notifyAll();
    }
}
