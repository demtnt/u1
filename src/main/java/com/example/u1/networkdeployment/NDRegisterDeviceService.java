package com.example.u1.networkdeployment;

import com.example.u1.model.Device;
import com.example.u1.model.RegisterDeviceRequest;
import java.util.ArrayList;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class NDRegisterDeviceService {

    private final NDRepository ndRepository;

    public void run(RegisterDeviceRequest registerDeviceRequest) {
        final var newDevice = new Device()
                .deviceType(registerDeviceRequest.getDeviceType())
                .mACAddress(registerDeviceRequest.getmACAddress())
                .uplink(registerDeviceRequest.getUplinkMACAddress())
                .downlink(new ArrayList<>());

        if (!hasCorrectUplinkAddress(newDevice)) {
            throw new RuntimeException();
        }

        ndRepository.getStorage().compute(registerDeviceRequest.getmACAddress(), (k, v) -> putOrThrow(v, newDevice));

        if (registerDeviceRequest.getUplinkMACAddress() != null) {
            ndRepository.getStorage().compute(registerDeviceRequest.getUplinkMACAddress(), (k, v) -> updateOrThrow(v, newDevice));
        }
    }

    Device putOrThrow(Device existingDevice, Device newDevice) {
        if (existingDevice != null) {
            throw new RuntimeException();
        }

        return newDevice;
    }

    Device updateOrThrow(Device uplinkDevice, Device currentDevice) {
        if (uplinkDevice == null) {
            throw new RuntimeException();
        }

        if (!uplinkDevice.getDownlink().contains(currentDevice.getmACAddress())) {
            uplinkDevice.getDownlink().add(currentDevice.getmACAddress());
        }

        return uplinkDevice;
    }

    boolean hasCorrectUplinkAddress(Device newDevice) {
        return newDevice.getUplink() == null
                || newDevice.getUplink().equals(newDevice.getmACAddress())
                || ndRepository.getStorage().containsKey(newDevice.getUplink());
    }
}
