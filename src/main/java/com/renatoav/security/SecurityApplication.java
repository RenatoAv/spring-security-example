package com.renatoav.security;

import com.renatoav.security.entity.Role;
import com.renatoav.security.entity.Usuario;
import com.renatoav.security.service.RoleService;
import com.renatoav.security.service.UsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Arrays;

@SpringBootApplication
public class SecurityApplication {

	public static void main(String[] args) {
		SpringApplication.run(SecurityApplication.class, args);
	}

	@Bean
	CommandLineRunner run(RoleService roleService, UsuarioService usuarioService, PasswordEncoder encoder) {
		return args -> {
			Role user = roleService.salvar(new Role("ROLE_USER"));
			Role adm = roleService.salvar(new Role("ROLE_ADMIN"));
			usuarioService.salvar(new Usuario("a", "a", encoder.encode("a"), Arrays.asList(user)));
			usuarioService.salvar(new Usuario("b", "b", encoder.encode("a"), Arrays.asList(adm)));
		};
	}

}
