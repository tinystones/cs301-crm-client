package com.g4t1.client.exceptions;

public class ExistingClientUUIDException extends RuntimeException {
    public ExistingClientUUIDException(){
        super("client already has an id");
    }
}
