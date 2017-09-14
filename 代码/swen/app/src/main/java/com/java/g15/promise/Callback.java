package com.java.g15.promise;

public interface Callback<IN,OUT>
{
    OUT run(IN result) throws Exception;
}
