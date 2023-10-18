package com.wild.security.controller;

import java.util.HashMap;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.wild.security.dto.UserDto;
import com.wild.security.jwt.JwtUtilities;
import com.wild.security.service.UserService;
import com.wild.security.utility.ApiResponse;

@RestController
public class UserController {

    private UserService userService;
    private JwtUtilities jwtUtilities;

    public UserController(UserService userService, JwtUtilities jwtUtilities) {
        this.userService = userService;
        this.jwtUtilities = jwtUtilities;
    }
    
    @PostMapping("/register")
    ResponseEntity<ApiResponse<Object>> register(@RequestBody UserDto user) {
        HashMap<String, Object> data = new HashMap<>();
        //HasMap = collection de paires clé-valeur, où chaque clé est unique
        try {
            userService.register(user);
            String token = jwtUtilities.generateToken(user);
            data.put("user", user);
            data.put("token", token);
            return new ResponseEntity<>(new ApiResponse<>(data), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    
    @PostMapping("/login")
    public ResponseEntity<ApiResponse<Object>> login(@RequestBody UserDto user) {
        HashMap<String, Object> data = new HashMap<>();
        try {
            userService.login(user);
            String token = jwtUtilities.generateToken(user);
            data.put("user", user);
            data.put("token", token);
            return new ResponseEntity<>(new ApiResponse<>(data), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/admin")
    public ResponseEntity<ApiResponse<Object>> onlyAdminData() {
        HashMap<String, Object> data = new HashMap<>();
        try {
            data.put("message", "This is only for admin");
            return new ResponseEntity<>(new ApiResponse<>(data), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/user-admin")
    public ResponseEntity<ApiResponse<Object>> userAdminData() {
        HashMap<String, Object> data = new HashMap<>();
        try {
        data.put("message", "This is for user and admin");
            return new ResponseEntity<>(new ApiResponse<>(data), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }

    @GetMapping("/multipass")
    public ResponseEntity<ApiResponse<Object>> permit() {
        HashMap<String, Object> data = new HashMap<>();
        try {
            data.put("message", "This is for all");
            return new ResponseEntity<>(new ApiResponse<>(data), HttpStatus.OK);
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return new ResponseEntity<>(new ApiResponse<>(e.getMessage()), HttpStatus.BAD_REQUEST);
        }
    }
}
