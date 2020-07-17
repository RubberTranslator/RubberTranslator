package com.rubbertranslator.listener;

@FunctionalInterface
public interface GenericCallback<T> {
    void callBack(T t);
}
