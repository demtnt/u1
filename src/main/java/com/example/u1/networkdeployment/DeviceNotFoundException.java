package com.example.u1.networkdeployment;

public class DeviceNotFoundException extends RuntimeException{

    public DeviceNotFoundException(String mac) {
        super(String.format("Device with mac %s not found", mac));
    }
}
