package com.crashdata.back.service;

public class InvalidCrashException extends RuntimeException{

    public InvalidCrashException(String message){
        super(message);
    }
}
