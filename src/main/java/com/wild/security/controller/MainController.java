package com.wild.security.controller;

import java.util.HashMap;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class MainController {
    
    @GetMapping("/")
    public ResponseEntity<?> index() {
        HashMap<String, Object> data = new HashMap<>();
        data.put("message", "coucou");
        return new ResponseEntity<>(data, null, 200);
    }

}
