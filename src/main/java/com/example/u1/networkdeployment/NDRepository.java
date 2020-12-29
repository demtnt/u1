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

}