package com.example.u1.networkdeployment;

import com.example.u1.model.Device;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static com.example.u1.model.DeviceType.ACCESS_POINT;
import static com.example.u1.model.DeviceType.GATEWAY;
import static com.example.u1.model.DeviceType.SWITCH;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NDServiceTest {

    @Mock
    Map<String, Device> networkDeploymentMap;

    @Mock
    NDRepository ndRepository;


    @InjectMocks
    private NDService subject;

    @Captor
    private ArgumentCaptor<String> mACArgument;

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void getByMAC_NOT_FOUND() {
        when(ndRepository.getStorage()).thenReturn(Collections.emptyMap());
        final var mAC = System.currentTimeMillis() + "";

        assertThrows(DeviceNotFoundException.class, () -> subject.getByMAC(mAC));
    }

    @Test
    void getByMAC() {
        final var expectedDevice = new Device();
        when(networkDeploymentMap.get(anyString())).thenReturn(expectedDevice);
        when(ndRepository.getStorage()).thenReturn(networkDeploymentMap);

        final var mAC = System.currentTimeMillis() + "";

        final var result = subject.getByMAC(mAC);
        assertEquals(expectedDevice.getmACAddress(), result.getmACAddress());
        assertEquals(expectedDevice.getDeviceType(), result.getDeviceType());

        verify(ndRepository).getStorage();
        verify(networkDeploymentMap).get(mACArgument.capture());
        assertEquals(mAC, mACArgument.getValue());
    }

    @Test
    void getAll() {
        final var unsortedList = List.of(
                new Device().mACAddress("11-11-11-11-11-11").deviceType(SWITCH),
                new Device().mACAddress("22-22-22-22-22-22").deviceType(GATEWAY),
                new Device().mACAddress("33-33-33-33-33-33").deviceType(SWITCH),
                new Device().mACAddress("44-44-44-44-44-44").deviceType(ACCESS_POINT),
                new Device().mACAddress("55-55-55-55-55-55").deviceType(GATEWAY)
                );
        when(networkDeploymentMap.values()).thenReturn(unsortedList);
        when(ndRepository.getStorage()).thenReturn(networkDeploymentMap);

        final var result = subject.getAll();
        assertEquals(GATEWAY, result.get(0).getDeviceType());
        assertEquals(GATEWAY, result.get(1).getDeviceType());
        assertEquals(SWITCH, result.get(2).getDeviceType());
        assertEquals(SWITCH, result.get(3).getDeviceType());
        assertEquals(ACCESS_POINT, result.get(4).getDeviceType());

        verify(ndRepository).getStorage();
        verify(networkDeploymentMap).values();
    }
}