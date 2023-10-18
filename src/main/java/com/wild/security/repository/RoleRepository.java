package com.wild.security.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.wild.security.entity.EnumRole;
import com.wild.security.entity.Role;

@Repository
public interface RoleRepository extends JpaRepository<Role,Long>{
    
    Role findByName(EnumRole name);
}