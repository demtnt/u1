package com.example.u1.networkdeployment;

import com.example.u1.model.Device;
import com.example.u1.model.DeviceInfo;
import com.example.u1.util.DeviceTypeComparator;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class NDService {

    private final NDRepository ndRepository;

    public List<DeviceInfo> getAll() {
        return ndRepository.getStorage().values()
                .stream()
                .sorted(this::compareDevices)
                .map(device -> new DeviceInfo()
                        .mACAddress(device.getmACAddress())
                        .deviceType(device.getDeviceType())
                )
                .collect(Collectors.toList());
    }

    public DeviceInfo getByMAC(String mAC) {
        final var device = ndRepository.getStorage().get(mAC);
        if (device == null) {
            throw new DeviceNotFoundException(mAC);
        }
        return new DeviceInfo()
                .mACAddress(device.getmACAddress())
                .deviceType(device.getDeviceType());
    }

    private int compareDevices(Device device1, Device device2) {
        return DeviceTypeComparator.get().compare(device1.getDeviceType(), device2.getDeviceType());
    }
}