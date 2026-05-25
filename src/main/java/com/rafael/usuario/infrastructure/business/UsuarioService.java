package com.rafael.usuario.infrastructure.business;

import com.rafael.usuario.infrastructure.business.converter.UsuarioConverter;
import com.rafael.usuario.infrastructure.business.dto.UsuarioDTO;
import com.rafael.usuario.infrastructure.business.dto.UsuarioResponseDTO;
import com.rafael.usuario.infrastructure.entity.Usuario;
import com.rafael.usuario.infrastructure.exceptions.ConflictException;
import com.rafael.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UsuarioResponseDTO salvarUsuario(UsuarioDTO usuarioDTO) {

        // Verifica se email já existe
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new ConflictException("Já existe um usuário cadastrado com o email: " + usuarioDTO.getEmail());
        }

        // Converte DTO → Entity
        Usuario usuario = usuarioConverter.toEntity(usuarioDTO);

        // Criptografa a senha ANTES de salvar
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        // Salva no banco
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // Retorna ResponseDTO (sem senha)
        return usuarioConverter.toResponseDTO(usuarioSalvo);
    }
}