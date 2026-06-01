package com.rafael.usuario.infrastructure.business;

import com.rafael.usuario.infrastructure.business.converter.UsuarioConverter;
import com.rafael.usuario.infrastructure.business.dto.EnderecoDTO;
import com.rafael.usuario.infrastructure.business.dto.SenhaUpdateDTO;
import com.rafael.usuario.infrastructure.business.dto.TelefoneDTO;
import com.rafael.usuario.infrastructure.business.dto.UsuarioDTO;
import com.rafael.usuario.infrastructure.business.dto.UsuarioResponseDTO;
import com.rafael.usuario.infrastructure.business.dto.UsuarioUpdateDTO;
import com.rafael.usuario.infrastructure.entity.Enderecos;
import com.rafael.usuario.infrastructure.entity.Telefones;
import com.rafael.usuario.infrastructure.entity.Usuario;
import com.rafael.usuario.infrastructure.exceptions.ConflictException;
import com.rafael.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.rafael.usuario.infrastructure.repository.EnderecosRepository;
import com.rafael.usuario.infrastructure.repository.TelefonesRepository;
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
    private final EnderecosRepository enderecosRepository;
    private final TelefonesRepository telefonesRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;

    // ====================== MÉTODOS EXISTENTES ======================

    @Transactional
    public UsuarioResponseDTO salvarUsuario(UsuarioDTO usuarioDTO) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new ConflictException("Já existe um usuário cadastrado com o email: " + usuarioDTO.getEmail());
        }

        Usuario usuario = usuarioConverter.toEntity(usuarioDTO);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return usuarioConverter.toResponseDTO(usuarioSalvo);
    }

    public UsuarioResponseDTO buscarUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o email: " + email));

        return usuarioConverter.toResponseDTO(usuario);
    }

    @Transactional
    public void deletarUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o email: " + email));

        usuarioRepository.delete(usuario);
    }

    @Transactional
    public UsuarioResponseDTO updateMe(UsuarioUpdateDTO dto) {
        String emailAtual = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(emailAtual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (dto.getEmail() != null && !dto.getEmail().equals(usuario.getEmail())) {
            if (usuarioRepository.existsByEmail(dto.getEmail())) {
                throw new ConflictException("Já existe um usuário cadastrado com este email: " + dto.getEmail());
            }
        }

        usuarioConverter.updateEntityFromDTO(usuario, dto);

        Usuario usuarioAtualizado = usuarioRepository.save(usuario);
        return usuarioConverter.toResponseDTO(usuarioAtualizado);
    }

    @Transactional
    public void atualizarSenha(SenhaUpdateDTO dto) {
        String emailAtual = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(emailAtual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(dto.getSenhaAtual(), usuario.getSenha())) {
            throw new RuntimeException("Senha atual incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(dto.getSenhaNova()));
        usuarioRepository.save(usuario);
    }

    // ====================== NOVOS MÉTODOS: ADIÇÃO DE ENDEREÇO E TELEFONE ======================

    @Transactional
    public EnderecoDTO adicionarEndereco(Long usuarioId, EnderecoDTO dto) {
        // 1. Pega o usuário autenticado pelo token JWT
        String emailAtual = SecurityContextHolder.getContext().getAuthentication().getName();

        // 2. Busca o usuário no banco para validar ownership
        Usuario usuario = usuarioRepository.findByEmail(emailAtual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // 3. Verifica se o usuarioId da URL pertence realmente ao usuário logado
        if (!usuario.getId().equals(usuarioId)) {
            throw new ResourceNotFoundException("Você não tem permissão para adicionar endereço neste usuário");
        }

        // 4. Converte DTO → Entity
        Enderecos endereco = usuarioConverter.toEnderecoEntity(dto, usuario);

        // 5. Salva no banco
        Enderecos enderecoSalvo = enderecosRepository.save(endereco);

        // 6. Retorna o DTO com os dados salvos (incluindo o ID gerado)
        return usuarioConverter.toEnderecoDTO(enderecoSalvo);
    }

    @Transactional
    public TelefoneDTO adicionarTelefone(Long usuarioId, TelefoneDTO dto) {
        String emailAtual = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(emailAtual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!usuario.getId().equals(usuarioId)) {
            throw new ResourceNotFoundException("Você não tem permissão para adicionar telefone neste usuário");
        }

        Telefones telefone = usuarioConverter.toTelefoneEntity(dto, usuario);

        Telefones telefoneSalvo = telefonesRepository.save(telefone);

        return usuarioConverter.toTelefoneDTO(telefoneSalvo);
    }

    // ====================== MÉTODOS PRIVADOS ======================

    private void validarPropriedadeDoEndereco(Enderecos endereco, Long usuarioId) {
        if (endereco.getUsuario() == null || !endereco.getUsuario().getId().equals(usuarioId)) {
            throw new ResourceNotFoundException("Endereço não pertence ao usuário informado");
        }
    }

    private void validarPropriedadeDoTelefone(Telefones telefone, Long usuarioId) {
        if (telefone.getUsuario() == null || !telefone.getUsuario().getId().equals(usuarioId)) {
            throw new ResourceNotFoundException("Telefone não pertence ao usuário informado");
        }
    }
}