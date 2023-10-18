package com.wild.security.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.wild.security.jwt.UserPrincipal;
import com.wild.security.repository.UserRepository;

@Service
public class CustomizedUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomizedUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return new UserPrincipal(userRepository.findByUsername(username).get());
    }
}
