package com.g4t1.client.exceptions;

public class ClientNotFoundException extends RuntimeException{
    public ClientNotFoundException(){
        super("client not found");
    }
}
