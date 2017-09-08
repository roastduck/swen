package com.swen.promise;

public interface Callback<IN,OUT>
{
    OUT run(IN result) throws Throwable;
}
