package com.g4t1.client.exceptions;

public class NoClientDataException extends RuntimeException {
    public NoClientDataException(){
        super("no client data to update");
    }
}
