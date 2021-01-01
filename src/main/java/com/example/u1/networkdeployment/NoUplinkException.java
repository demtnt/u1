package com.example.u1.networkdeployment;

public class NoUplinkException extends RuntimeException {
    public NoUplinkException(String mac) {
        super(String.format("Uplink device with MAC %s does not exist in deployment but wanted", mac));
    }
}
