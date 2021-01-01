package com.example.u1.networkdeployment;

public class DuplicateMACException extends RuntimeException {

    public DuplicateMACException(String mac) {
        super(String.format("Device with MAC %s already exist in deployment", mac));
    }
}
