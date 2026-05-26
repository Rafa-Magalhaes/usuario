package com.rafael.usuario.infrastructure.business;

import com.rafael.usuario.infrastructure.business.converter.UsuarioConverter;
import com.rafael.usuario.infrastructure.business.dto.UsuarioDTO;
import com.rafael.usuario.infrastructure.business.dto.UsuarioResponseDTO;
import com.rafael.usuario.infrastructure.controller.UsuarioController;
import com.rafael.usuario.infrastructure.entity.Usuario;
import com.rafael.usuario.infrastructure.exceptions.ConflictException;
import com.rafael.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.rafael.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.context.SecurityContextHolder;
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

    // ==================== BUSCAR POR EMAIL ====================
    public UsuarioResponseDTO buscarUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o email: " + email));

        return usuarioConverter.toResponseDTO(usuario);
    }

    // ==================== DELETAR POR EMAIL ====================
    @Transactional
    public void deletarUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o email: " + email));

        usuarioRepository.delete(usuario);
    }

    @Transactional
    public UsuarioResponseDTO updateMe(infrastructure.business.dto.UsuarioUpdateDTO dto) {
        // Obtém o email do usuário autenticado via JWT
        String emailAtual = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(emailAtual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // Verifica se o email está sendo alterado e se já existe
        if (dto.getEmail() != null && !dto.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new ConflictException("Já existe um usuário cadastrado com este email: " + dto.getEmail());
            }
        }

        // Aplica as atualizações parciais
        usuarioConverter.updateEntityFromDTO(usuario, dto);

        // Criptografa senha somente se foi enviada
        if (dto.getSenha() != null && !dto.getSenha().isBlank()) {
            usuario.setSenha(passwordEncoder.encode(dto.getSenha()));
        }

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return usuarioConverter.toResponseDTO(usuarioAtualizado);
    }
}