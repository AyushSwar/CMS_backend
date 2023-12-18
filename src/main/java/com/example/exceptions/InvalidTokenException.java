package com.example.exceptions;

public class InvalidTokenException extends RuntimeException {
     public InvalidTokenException(String message){
         super(message);
     }
}
