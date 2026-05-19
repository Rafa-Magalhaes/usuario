package com.rafael.usuario.infrastructure.business;

import com.rafael.usuario.infrastructure.business.converter.UsuarioConverter;
import com.rafael.usuario.infrastructure.business.dto.UsuarioDTO;
import com.rafael.usuario.infrastructure.business.dto.UsuarioResponseDTO;
import com.rafael.usuario.infrastructure.entity.Usuario;
import com.rafael.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final UsuarioConverter usuarioConverter;

    @Transactional
    public UsuarioResponseDTO salvarUsuario(UsuarioDTO usuarioDTO) {

        // Converte DTO para Entity
        Usuario usuario = usuarioConverter.toEntity(usuarioDTO);

        // Salva no banco
        Usuario usuarioSalvo = usuarioRepository.save(usuario);

        // Converte Entity para ResponseDTO (sem senha e sem ID se você removeu)
        return usuarioConverter.toResponseDTO(usuarioSalvo);
    }
}