package com.example.assignment;

public interface Networkable<T> {
    public ServerResponse post() throws ServerResponseException;

    public T request() ;
}
