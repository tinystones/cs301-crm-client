package com.g4t1.client.exceptions;

public class NullClientException extends RuntimeException {
    public NullClientException(){
        super("client data must not be null");
    }
}
