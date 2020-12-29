package com.example.u1.api;

import com.example.u1.model.DeviceInfo;
import com.example.u1.model.RegisterDeviceRequest;
import com.example.u1.networkdeployment.NDRegisterDeviceService;
import com.example.u1.networkdeployment.NDService;
import java.util.List;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PathVariable;

@Slf4j
@Controller
@RequiredArgsConstructor
public class NetworkDeploymentController implements NetworkdeploymentApi {

    private final NDRegisterDeviceService ndRegisterDeviceService;
    private final NDService ndService;

    @Override
    public ResponseEntity<Void> addDevice(@Valid RegisterDeviceRequest registerDeviceRequest) {
        log.debug("Call addDevice with param {}", registerDeviceRequest);

        ndRegisterDeviceService.run(registerDeviceRequest);

        log.debug("AddDevice {} connected to {} success", registerDeviceRequest.getmACAddress(), registerDeviceRequest.getUplinkMACAddress());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Override
    public ResponseEntity<List<DeviceInfo>> getAll() {
        log.debug("Call getAll");

        final var all = ndService.getAll();

        log.debug("getAll response list size {} success", all.size());
        return ResponseEntity.ok(all);
    }

    @Override
    public ResponseEntity<DeviceInfo> getByMac(@PathVariable("mac") String mac) {
        log.debug("Call getByMac with param {}", mac);

        return ResponseEntity.ok(ndService.getByMAC(mac));
    }
}