package com.example.u1.networkdeployment;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class DeviceTopologyService {

    private final NDRepository ndRepository;

    public String printTree() {
        final var sb = new StringBuilder();

        // supporting multiple roots
        ndRepository.getStorage().values().forEach(currentDevice -> {
            if (currentDevice.getUplink() == null
                    || currentDevice.getUplink().equals(currentDevice.getmACAddress())) {
                sb.append(printDeviceSubtree(currentDevice.getmACAddress(), 0));
            }
        });

        return sb.toString();
    }

    public String printDeviceSubtree(String mac, int level) {
        final var device = ndRepository.getStorage().get(mac);
        if (device == null) {
            throw new DeviceNotFoundException(mac);
        }

        final var sb = new StringBuilder();

        sb.append(getPrefix(level)).append(device.getmACAddress()).append(System.lineSeparator());
        if (device.getDownlink() != null) {
            device.getDownlink().forEach(downMac -> {
                if (downMac.equals(device.getmACAddress())) {
                    // no stack overflow
                    return;
                }

                sb.append(printDeviceSubtree(downMac, level + 1));
            });
        }

        return sb.toString();
    }

    String getPrefix(int level) {
        if (level == 0) {
            return "";
        }
        return " ".repeat(level - 1) + "-";
    }
}