package com.wipro.vrs.util;

public class RentalOperationException extends Exception {

    public RentalOperationException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "RentalOperationException: " + getMessage();
    }
}
