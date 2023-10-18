package com.wild.security.service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import com.wild.security.dto.RoleDto;
import com.wild.security.dto.UserDto;
import com.wild.security.entity.EnumRole;
import com.wild.security.entity.Role;
import com.wild.security.entity.UserEntity;
import com.wild.security.repository.RoleRepository;
import com.wild.security.repository.UserRepository;

@Service
public class UserService {
    
    private RoleRepository roleRepository;
    private UserRepository userRepository;
    private BCryptPasswordEncoder bcryptEncoder;
    public UserService(RoleRepository roleRepository, UserRepository userRepository,
            BCryptPasswordEncoder bcryptEncoder) {
        this.roleRepository = roleRepository;
        this.userRepository = userRepository;
        this.bcryptEncoder = bcryptEncoder;
    }

    public boolean checkHashedPassword(String password) {
        // Réelle implémantation à mettre en place vous même
        return true;
    }
    
    public boolean checkEmail(String email) {
        // Réelle implémantation à mettre en place vous même
        return true;
    }

    // ############## REGISTER #####################
    public UserDto register(UserDto user) {
        if (!checkHashedPassword(user.getPassword())) {
            throw new RuntimeException("Le mot de passe n'est pas assez fort");
        }
        if (!checkEmail(user.getEmail())) {
            throw new RuntimeException("L'email existe déja");
        }

        //hashage du mot de passe
        String hashedPassword = bcryptEncoder.encode(user.getPassword());

        // Création d'une liste pour récupérer les rôles
        List<Role> userRoles = new ArrayList<>();  

        for (RoleDto roleDto : user.getRoles()) {
            EnumRole enumRole = EnumRole.valueOf(roleDto.getName()); // Converti le nom du rôle en EnumRole
            Role userRole = roleRepository.findByName(enumRole);// Récupére le rôle 
            if (userRole != null) {
                userRoles.add(userRole);
            } 
        }
        // créé et sauvegarde le nouvel user
        UserEntity newUser = new UserEntity(user.getUsername(), user.getEmail(), hashedPassword, userRoles, false);
        
        // Création d'un nouvel UserDto pour le retour
        UserDto newUserDto = new UserDto();
        newUserDto.setUsername(newUser.getUsername());
        newUserDto.setEmail(newUser.getEmail());

        // Création d'une liste pour stocker les rôles convertis en RoleDto
        List<RoleDto> rolesDto = new ArrayList<>(); 

        // Parcours les rôles de l'utilisateur
        for (Role role : newUser.getRoles()) {
            RoleDto roleDto = new RoleDto(role.getName().toString()); // Création d'une nouvelle liste de roleDto à partie des roles de l'user
            rolesDto.add(roleDto); 
        }

        // Ajout de la liste des rôles à l'UserDto
        newUserDto.setRoles(rolesDto);

        userRepository.save(newUser); 
        return newUserDto;
    }

     // ############## LOGIN #####################
    public boolean verifyHashedPasswordDuringLogin(String password, String hashedPassword) {
        return bcryptEncoder.matches(password, hashedPassword);
    }

    public UserEntity getUserEntityByEmail(String email) {
        try {
            return userRepository.findByEmail(email).get();
        } catch (Exception e) {
            throw new RuntimeException("L'email n'existe pas");
        }
    }

    public UserDto login(UserDto user) {
        UserEntity userEntity = getUserEntityByEmail(user.getEmail());
        if (!verifyHashedPasswordDuringLogin(user.getPassword(), userEntity.getPassword())) {
            throw new RuntimeException("Le mot de passe est incorrect");
        }

        List<RoleDto> roleDtos = userEntity.getRoles()
            .stream()
            .map(role -> new RoleDto(role.getName().toString()))
            .collect(Collectors.toList());

        user.setRoles(roleDtos);

        return user; // pas nécéssaire mais permet de vérifier que cela fonctionne
    }
}
