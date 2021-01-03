package com.example.u1.networkdeployment;

import com.example.u1.model.Device;
import java.util.List;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.intThat;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DeviceTopologyServiceTest {

    @Mock
    NDRepository ndRepository;

    @InjectMocks
    @Spy
    private DeviceTopologyService subject;

    private Map<String, Device> multipleRootStorage;
    private Map<String, Device> bigStorage;

    @BeforeEach
    void setUp() {
        multipleRootStorage = Map.of(
                "single_root", new Device().mACAddress("single_root"),
                "self_root", new Device().mACAddress("self_root").uplink("self_root"),
                "stackOverflow", new Device().mACAddress("stackOverflow").uplink("stackOverflow").downlink(List.of("stackOverflow")),
                "1", new Device().mACAddress("1").downlink(List.of("11", "12")),
                "11", new Device().mACAddress("11").uplink("1"),
                "12", new Device().mACAddress("12").uplink("1")
        );

        bigStorage = Map.of(
                "1", new Device().mACAddress("1").downlink(List.of("2", "11", "12", "13", "14")),
                "11", new Device().mACAddress("11").uplink("1"),
                "12", new Device().mACAddress("12").uplink("1"),
                "13", new Device().mACAddress("13").uplink("1"),
                "14", new Device().mACAddress("14").uplink("1"),
                "2", new Device().mACAddress("2").downlink(List.of("3")).uplink("1"),
                "3", new Device().mACAddress("3").downlink(List.of("4", "44")).uplink("2"),
                "4", new Device().mACAddress("4").downlink(List.of("5")).uplink("3"),
                "44", new Device().mACAddress("44").uplink("3"),
                "5", new Device().mACAddress("5").uplink("4")
        );
    }

    @Test
    void printTree() {
        when(ndRepository.getStorage()).thenReturn(multipleRootStorage);
        doReturn("zz").when(subject).printDeviceSubtree(anyString(), eq(0));

        final var result = subject.printTree();

        verify(subject, times(4)).printDeviceSubtree(anyString(), eq(0));

        assertEquals("zz".repeat(4), result);
    }

    @Test
    void printDeviceSubtree_WRONG_MAC() {
        when(ndRepository.getStorage()).thenReturn(bigStorage);

        final var notExistingMAC = "" + System.currentTimeMillis();

        assertThrows(DeviceNotFoundException.class, () -> subject.printDeviceSubtree(notExistingMAC, 0));
    }

    @Test
    void printDeviceSubtree_NO_CHILDREN() {
        when(ndRepository.getStorage()).thenReturn(multipleRootStorage);

        final var result = subject.printDeviceSubtree("single_root", 0);

        verify(subject, times(0)).printDeviceSubtree(anyString(), intThat(curLevel -> curLevel > 0));
        assertEquals("single_root" + System.lineSeparator(), result);
    }

    @Test
    void printDeviceSubtree_NO_STACK_OVERFLOW() {
        when(ndRepository.getStorage()).thenReturn(multipleRootStorage);

        final var result = subject.printDeviceSubtree("stackOverflow", 0);

        verify(subject, times(0)).printDeviceSubtree(anyString(), intThat(curLevel -> curLevel > 0));
        assertEquals("stackOverflow" + System.lineSeparator(), result);
    }

    @Test
    void printDeviceSubtree() {
        when(ndRepository.getStorage()).thenReturn(multipleRootStorage);
        doReturn("aaa").when(subject).printDeviceSubtree(anyString(), eq(1));

        final var result = subject.printDeviceSubtree("1", 0);

        verify(subject, times(2)).printDeviceSubtree(anyString(), eq(1));
        assertEquals("1" + System.lineSeparator() + "aaa".repeat(2), result);
    }

    @Test
    void prefixNO() {
        assertEquals("", subject.getPrefix(0));
    }

    @Test
    void prefix() {
        assertEquals("       -", subject.getPrefix(8));
    }
}