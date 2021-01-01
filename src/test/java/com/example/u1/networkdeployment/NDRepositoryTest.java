package com.example.u1.networkdeployment;

import com.example.u1.model.Device;
import java.util.ArrayList;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(MockitoExtension.class)
class NDRepositoryTest {

    @Spy
    private NDRepository subject;


    @Test
    void putOrThrow_ALREADY_EXISTS() {
        final var valueFromMap = new Device();
        final var newDevice = new Device();
        assertThrows(DuplicateMACException.class, () -> subject.putOrThrow(valueFromMap, newDevice));
    }

    @Test
    void putOrThrow() {
        final var newDevice = new Device();
        assertSame(newDevice, subject.putOrThrow(null, newDevice));
    }

    @Test
    void updateOrThrow_NO_UPLINK_DEVICE() {
        final var currentDevice = new Device();
        assertThrows(NoUplinkException.class, () -> subject.updateOrThrow(null, currentDevice));
    }

    @Test
    void updateOrThrow_ALREADY_UPDATED() {
        final var similarMAC = "MAC_" + System.currentTimeMillis();
        final var currentDevice = new Device().mACAddress(similarMAC);
        final var uplinkDevice = new Device().downlink(Collections.singletonList(similarMAC));

        final var result = subject.updateOrThrow(uplinkDevice, currentDevice);

        assertEquals(1, result.getDownlink().size());
    }

    @Test
    void updateOrThrow() {
        final var currentDevice = new Device().mACAddress("MAC_1");
        final var uplinkDevice = new Device().downlink(new ArrayList<>());

        final var result = subject.updateOrThrow(uplinkDevice, currentDevice);

        assertEquals(currentDevice.getmACAddress(), result.getDownlink().get(0));
    }
}