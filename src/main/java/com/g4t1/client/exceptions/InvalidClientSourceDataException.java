package com.g4t1.client.exceptions;

public class InvalidClientSourceDataException extends IllegalArgumentException{
    public InvalidClientSourceDataException(){
        super("invalid client source data, please check fields");
    }
}
