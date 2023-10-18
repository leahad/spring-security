package com.wild.security;

import java.util.Arrays;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import com.wild.security.entity.Role;
import com.wild.security.entity.EnumRole;
import com.wild.security.entity.UserEntity;
import com.wild.security.jwt.JwtUtilities;
import com.wild.security.repository.RoleRepository;
import com.wild.security.repository.UserRepository;

@SpringBootApplication
public class SecurityApplication {

	private UserRepository userRepository;
	private BCryptPasswordEncoder bcryptEncoder;
	private RoleRepository roleRepository;
	private JwtUtilities jwtUtilities;


	public SecurityApplication(UserRepository userRepository, BCryptPasswordEncoder passwordEncoder, RoleRepository roleRepository, JwtUtilities jwtUtilities) {
		this.userRepository = userRepository;
		this.bcryptEncoder = passwordEncoder;
		this.roleRepository = roleRepository;
		this.jwtUtilities = jwtUtilities;
	}

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

	@Bean
	public CommandLineRunner run() throws Exception {	
		return (String[] args) -> {
			Role adminRole = roleRepository.save(new Role(EnumRole.ADMIN));
			Role userRole = roleRepository.save(new Role(EnumRole.USER));

			UserEntity user1 = new UserEntity("Léa", "lea@gmail.com", bcryptEncoder.encode("password"), Arrays.asList(adminRole, userRole), false);
			userRepository.save(user1);

			UserEntity user2 = new UserEntity("Eri", "erika@gmail.com", bcryptEncoder.encode("password"), Arrays.asList(userRole), false);
			userRepository.save(user2);

			// Génére et attribue un token JWT pour chaque utilisateur enregistré
			String token1 = jwtUtilities.generateToken(user1.toUserDto());
			String token2 = jwtUtilities.generateToken(user2.toUserDto());

			System.out.println("Token for user1: " + token1);
			System.out.println("Token for user2: " + token2);
		};
	}
}
