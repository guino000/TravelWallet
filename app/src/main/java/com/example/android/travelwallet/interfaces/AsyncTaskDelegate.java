package com.example.android.travelwallet.interfaces;

public interface AsyncTaskDelegate<T> {
    void processFinish(T output);
}
