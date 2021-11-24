package com.renatoav.security.service;

import com.renatoav.security.entity.Role;
import com.renatoav.security.repository.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RoleServiceImpl implements RoleService {

    private final RoleRepository roleRepository;
    @Override
    public Role salvar(Role role) {
        return roleRepository.save(role);
    }
}
