package com.renatoav.security.service;

import com.renatoav.security.entity.Usuario;

import java.util.List;
import java.util.Optional;

public interface UsuarioService {
    List<Usuario> listar();
    Optional<Usuario> obterPorId(Long id);
    void salvar(Usuario usuario);
    void deletar();
    Usuario obterUsuarioPorUsername(String subject);
}
