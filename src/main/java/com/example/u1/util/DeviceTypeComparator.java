package com.example.u1.util;

import com.example.u1.model.DeviceType;
import java.util.Comparator;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

public class DeviceTypeComparator implements Comparator<DeviceType> {
    private final Map<DeviceType, Integer> itemsToIndex;

    private DeviceTypeComparator(List<DeviceType> items) {
        itemsToIndex = new EnumMap<>(DeviceType.class);

        int index = 0;
        for (DeviceType item : items) {
            if (itemsToIndex.containsKey(item)) {
                throw new IllegalArgumentException("Inconsistent ranks: there should be no duplicates");
            }
            itemsToIndex.put(item, ++index);
        }
    }

    public static Comparator<DeviceType> get() {
        return new DeviceTypeComparator(List.of(DeviceType.GATEWAY, DeviceType.SWITCH, DeviceType.ACCESS_POINT));
    }

    @Override
    public int compare(DeviceType o1, DeviceType o2) {
        return Integer.compare(itemsToIndex.get(o1), itemsToIndex.get(o2));
    }
}