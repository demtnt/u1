package com.example.u1.networkdeployment;

import com.example.u1.model.Device;
import com.example.u1.model.DeviceType;
import com.example.u1.model.RegisterDeviceRequest;
import java.util.Collections;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NDRegisterDeviceServiceTest {

    @Mock
    NDRepository ndRepository;

    @InjectMocks
    @Spy
    private NDRegisterDeviceService subject;

    @Captor
    private ArgumentCaptor<Device> newDeviceArgument;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void run_INCORRECT_UPLINK() {
        doReturn(false).when(subject).hasCorrectUplinkAddress(any());

        final var registerDeviceRequest = new RegisterDeviceRequest();

        assertThrows(NoUplinkException.class, () -> subject.run(registerDeviceRequest));
    }

    @Test
    void run_ALREADY_TAKEN_MAC() {
        final var expectedMAC = "mac " + System.currentTimeMillis();
        final var expectedDeviceType = DeviceType.ACCESS_POINT;
        final var expectedUplink = "uplink " + System.currentTimeMillis();

        doReturn(true).when(subject).hasCorrectUplinkAddress(any());
        doThrow(new DuplicateMACException("mac")).when(ndRepository).addUnique(any());

        final var registerDeviceRequest = new RegisterDeviceRequest()
                .mACAddress(expectedMAC)
                .deviceType(expectedDeviceType)
                .uplinkMACAddress(expectedUplink);

        assertThrows(DuplicateMACException.class, () -> subject.run(registerDeviceRequest));

        verify(subject).hasCorrectUplinkAddress(newDeviceArgument.capture());
        verify(ndRepository).addUnique(newDeviceArgument.capture());
        verify(ndRepository, times(0)).addUplink(any());

        newDeviceArgument.getAllValues().forEach(curDeviceArgument -> {
            assertEquals(expectedMAC, curDeviceArgument.getmACAddress());
            assertEquals(expectedDeviceType, curDeviceArgument.getDeviceType());
            assertEquals(expectedUplink, curDeviceArgument.getUplink());
        });
    }

    @Test
    void run_WRONG_UPLINK_DEVICE() {
        final var expectedMAC = "mac " + System.currentTimeMillis();
        final var expectedDeviceType = DeviceType.ACCESS_POINT;
        final var expectedUplink = "uplink " + System.currentTimeMillis();

        doReturn(true).when(subject).hasCorrectUplinkAddress(any());
        doNothing().when(ndRepository).addUnique(any());
        doThrow(NoUplinkException.class).when(ndRepository).addUplink(any());

        final var registerDeviceRequest = new RegisterDeviceRequest()
                .mACAddress(expectedMAC)
                .deviceType(expectedDeviceType)
                .uplinkMACAddress(expectedUplink);

        assertThrows(NoUplinkException.class, () -> subject.run(registerDeviceRequest));

        verify(subject).hasCorrectUplinkAddress(newDeviceArgument.capture());
        verify(ndRepository).addUnique(newDeviceArgument.capture());
        verify(ndRepository).addUplink(newDeviceArgument.capture());

        newDeviceArgument.getAllValues().forEach(curDeviceArgument -> {
            assertEquals(expectedMAC, curDeviceArgument.getmACAddress());
            assertEquals(expectedDeviceType, curDeviceArgument.getDeviceType());
            assertEquals(expectedUplink, curDeviceArgument.getUplink());
        });
    }

    @Test
    void run() {
        final var expectedMAC = "mac " + System.currentTimeMillis();
        final var expectedDeviceType = DeviceType.ACCESS_POINT;
        final var expectedUplink = "uplink " + System.currentTimeMillis();

        doReturn(true).when(subject).hasCorrectUplinkAddress(any());
        doNothing().when(ndRepository).addUnique(any());
        doNothing().when(ndRepository).addUplink(any());

        final var registerDeviceRequest = new RegisterDeviceRequest()
                .mACAddress(expectedMAC)
                .deviceType(expectedDeviceType)
                .uplinkMACAddress(expectedUplink);

        subject.run(registerDeviceRequest);

        verify(subject).hasCorrectUplinkAddress(newDeviceArgument.capture());
        verify(ndRepository).addUnique(newDeviceArgument.capture());
        verify(ndRepository).addUplink(newDeviceArgument.capture());

        newDeviceArgument.getAllValues().forEach(curDeviceArgument -> {
            assertEquals(expectedMAC, curDeviceArgument.getmACAddress());
            assertEquals(expectedDeviceType, curDeviceArgument.getDeviceType());
            assertEquals(expectedUplink, curDeviceArgument.getUplink());
        });
    }

    @Test
    void hasCorrectUplinkAddress_NO_UPLINK() {
        final var newDevice = new Device()
                .mACAddress("MAC")
                .deviceType(DeviceType.ACCESS_POINT)
                .uplink(null);

        assertTrue(subject.hasCorrectUplinkAddress(newDevice));
    }

    @Test
    void hasCorrectUplinkAddress_SELF_UPLINK() {
        final var similarMAC = "MAC" + System.currentTimeMillis();

        final var newDevice = new Device()
                .mACAddress(similarMAC)
                .deviceType(DeviceType.ACCESS_POINT)
                .uplink(similarMAC);

        assertTrue(subject.hasCorrectUplinkAddress(newDevice));
    }

    @Test
    void hasCorrectUplinkAddress() {
        final var similarMAC = "MAC" + System.currentTimeMillis();
        when(ndRepository.getStorage()).thenReturn(Collections.singletonMap(similarMAC, new Device()));

        final var newDevice = new Device()
                .mACAddress("MAC")
                .deviceType(DeviceType.ACCESS_POINT)
                .uplink(similarMAC);

        assertTrue(subject.hasCorrectUplinkAddress(newDevice));
    }
}