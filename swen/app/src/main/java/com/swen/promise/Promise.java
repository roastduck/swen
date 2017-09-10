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

    private Callback<IN,OUT> callback;

    private List< Promise<?,?> > parents;
    private List< Promise<OUT,?> > resNext;
    private List< Promise<Exception,?> > rejNext;

    boolean alreadyRun = false;
    private boolean resolved = false;
    private boolean rejected = false;
    OUT output;
    Exception throwable;

    private boolean runInUI = false;

    private Promise(Callback<IN,OUT> callback)
    {
        parents = new Vector<>();
        resNext = new Vector<>();
        rejNext = new Vector<>();
        this.callback = callback;
    }

    /** Create a Promise object
     *  @param callback : Callback run immediately after the object being constructed
     *  @param input : Input for the callback
     */
    public Promise(Callback<IN,OUT> callback, IN input)
    {
        this(callback);
        executorService.submit(new Runnable()
        {
            @Override
            public void run()
            {
                Promise.this.run(input);
            }
        });
    }

    private <T> Promise<OUT,T> thenPipe(Promise<OUT,T> next)
    {
        next.parents.add(this);
        if (!alreadyRun)
            resNext.add(next);
        else if (resolved)
            executorService.submit(new Runnable()
            {
                @Override
                public void run()
                {
                    next.run(output);
                }
            });
        return next;
    }

    private <T> Promise<Exception,T> failPipe(Promise<Exception,T> next)
    {
        for (Promise<?,?> parent : parents)
            parent.failPipe(next);
        next.parents.add(this);
        if (!alreadyRun)
            rejNext.add(next);
        else if (rejected)
            executorService.submit(new Runnable()
            {
                @Override
                public void run()
                {
                    next.run(throwable);
                }
            });
        return next;
    }

    /** What should be done after the Promise finished its callback normally
     *  You can attach more than 1 `then` callbacks by calling `then` multiple times
     *  @param callback : What you want do after the Promise finished its callback normally
     *  @return : A new Promise containing the new callback
     */
    public <T> Promise<OUT,T> then(Callback<OUT,T> callback)
    {
        return thenPipe(new Promise<OUT,T>(callback));
    }

    /** What should be done when this Promise AND ITS PARENT Promises failed to finish its callback
     *  You can attach more than 1 `fail` callbacks by calling `fail` multiple times
     *  @param callback : What you want to do after the Promise failed
     *  @return : A new Promise containing the new callback
     */
    public <T> Promise<Exception,T> fail(Callback<Exception,T> callback)
    {
        return failPipe(new Promise<Exception,T>(callback));
    }

    /** Same as `.then`, but run in UI thread
     */
    public <T> Promise<OUT,T> thenUI(Callback<OUT,T> callback)
    {
        Promise<OUT,T> ret = then(callback);
        ret.setRunInUI();
        return ret;
    }

    /** Same as `.fail`, but run in UI thread
     */
    public <T> Promise<Exception,T> failUI(Callback<Exception,T> callback)
    {
        Promise<Exception,T> ret = fail(callback);
        ret.setRunInUI();
        return ret;
    }

    public void setRunInUI() { runInUI = true; }

    private static final long DEFAULT_WAIT_TIMEOUT = 10000;

    /** Wait for this Promise to finish or fail with default timeout
     *  @throws InterruptedException : if time's out, throw an InterruptedException
     *  @return : this Promise
     */
    public Promise<IN,OUT> waitUntilHasRun() throws InterruptedException
    {
        return waitUntilHasRun(DEFAULT_WAIT_TIMEOUT);
    }

    /** Wait for this Promise to finish or fail
     *  @param timeout : if time's out, throw an InterruptedException
     *  @return : this Promise
     */
    public synchronized Promise<IN,OUT> waitUntilHasRun(long timeout) throws InterruptedException
    {
        if (alreadyRun)
            return this;
        wait(timeout);
        if (!alreadyRun)
            throw new InterruptedException();
        return this;
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
        try
        {
            output = callback.run(input);
            for (Promise<OUT,?> next : resNext)
                executorService.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        next.run(output);
                    }
                });
            resolved = true;
        } catch (Exception e)
        {
            throwable = e;
            for (Promise<Exception,?> next : rejNext)
                executorService.submit(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        next.run(throwable);
                    }
                });
            rejected = true;
        }
        resNext = null;
        rejNext = null;
        alreadyRun = true;
        notifyAll();
    }
}
