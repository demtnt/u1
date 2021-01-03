package com.example.u1.api;

import com.example.u1.networkdeployment.DeviceTopologyService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Slf4j
@Controller
@RequiredArgsConstructor
public class DeviceTopologyController implements DevicetopologyApi {

    private final DeviceTopologyService deviceTopologyService;

    @Override
    public ResponseEntity<String> getAllTopology() {
        log.debug("Call getAllTopology");

        return ResponseEntity.ok(deviceTopologyService.printTree());
    }

    @Override
    public ResponseEntity<String> getSubTreeTopology(String mac) {
        log.debug("Call getSubTreeTopology with param {}", mac);

        final var topologyTree = deviceTopologyService.printDeviceSubtree(mac, 0);
        return ResponseEntity.ok(topologyTree);
    }
}
