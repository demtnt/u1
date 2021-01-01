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
            throw new NoUplinkException(newDevice.getUplink());
        }

        ndRepository.addUnique(newDevice);

        if (newDevice.getUplink() != null) {
            ndRepository.addUplink(newDevice);
        }
    }

    boolean hasCorrectUplinkAddress(Device newDevice) {
        return newDevice.getUplink() == null
                || newDevice.getUplink().equals(newDevice.getmACAddress())
                || ndRepository.getStorage().containsKey(newDevice.getUplink());
    }
}
