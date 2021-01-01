package com.example.u1.networkdeployment;

import com.example.u1.model.Device;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import lombok.Getter;
import org.springframework.stereotype.Repository;

@Repository
public class NDRepository {

    @Getter
    private final Map<String, Device> storage = new ConcurrentHashMap<>();

    void addUnique(Device newDevice) {
        storage.compute(newDevice.getmACAddress(), (k, v) -> putOrThrow(v, newDevice));
    }

    Device putOrThrow(Device existingDevice, Device newDevice) {
        if (existingDevice != null) {
            throw new DuplicateMACException(existingDevice.getmACAddress());
        }

        return newDevice;
    }

    void addUplink(Device newDevice) {
        storage.compute(newDevice.getUplink(), (k, v) -> updateOrThrow(v, newDevice));
    }

    Device updateOrThrow(Device uplinkDevice, Device currentDevice) {
        if (uplinkDevice == null) {
            throw new NoUplinkException(currentDevice.getUplink());
        }

        if (!uplinkDevice.getDownlink().contains(currentDevice.getmACAddress())) {
            uplinkDevice.getDownlink().add(currentDevice.getmACAddress());
        }

        return uplinkDevice;
    }
}