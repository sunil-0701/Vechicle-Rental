package com.wipro.vrs.util;

public class VehicleNotAvailableException extends Exception {

    public VehicleNotAvailableException(String message) {
        super(message);
    }

    @Override
    public String toString() {
        return "VehicleNotAvailableException: " + getMessage();
    }
}
