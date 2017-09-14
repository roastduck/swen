package com.java.g15.promise;

import org.junit.Test;

import static org.junit.Assert.*;

public class PromiseTest
{
    @Test
    public void testThen() throws Exception
    {
        new Promise<Integer,Integer>(x -> x + 1, 1)
                .then(x -> x * 2)
                .then(x -> {
                    assertEquals(4, x.intValue());
                    return null;
                })
                .waitUntilHasRun();
    }

    @Test
    public void testFail() throws Exception
    {
        new Promise<Object,Object>(o -> {
            throw new Exception("except1");
        }, null)
                .fail(e -> {
                    assertEquals("except1", e.getMessage());
                    return null;
                })
                .waitUntilHasRun();
    }

    @Test
    public void testParentFail() throws Exception
    {
        new Promise<Object,Object>(o -> {
            throw new Exception("except1");
        }, null)
                .then(o -> 1)
                .fail(e -> {
                    assertEquals("except1", e.getMessage());
                    return null;
                })
                .waitUntilHasRun();
    }

    @Test
    public void testOnePromiseOnlyTriggeredOnce() throws Exception
    {
        Counter triggerNum1 = new Counter(0);
        Counter triggerNum2 = new Counter(0);
        new Promise<Object,Object>(o -> {
            throw new Exception("except1");
        }, null)
                .fail(e -> {
                    triggerNum1.num++;
                    throw new Exception("except2");
                })
                .fail(e -> {
                    triggerNum2.num++;
                    return null;
                })
                .waitUntilHasRun();
        assertEquals(1, triggerNum1.num);
        assertEquals(1, triggerNum2.num);
    }

    @Test
    public void testCancel() throws Exception
    {
        Counter num = new Counter(0);
        new Promise<Object,Object>(o -> {
            Thread.sleep(1000);
            return null;
        }, null)
                .then(o -> {
                    num.num++;
                    return null;
                })
                .cancel();
        Thread.sleep(2000);
        assertEquals(0, num.num);
    }

    private class Counter
    {
        int num;
        Counter(int num) { this.num = num; }
    }
}