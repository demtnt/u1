package com.example.u1.api;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;

@Controller
public class PingController implements PingApi {

    @Override
    public ResponseEntity<String> pingPong() {
        return ResponseEntity.ok("pong " + System.currentTimeMillis());
    }
}