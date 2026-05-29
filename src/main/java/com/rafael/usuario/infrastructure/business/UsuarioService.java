package com.rafael.usuario.infrastructure.business;

import com.rafael.usuario.infrastructure.business.converter.UsuarioConverter;
import com.rafael.usuario.infrastructure.business.dto.EnderecoDTO;
import com.rafael.usuario.infrastructure.business.dto.EnderecoUpdateDTO;
import com.rafael.usuario.infrastructure.business.dto.SenhaUpdateDTO;
import com.rafael.usuario.infrastructure.business.dto.TelefoneDTO;
import com.rafael.usuario.infrastructure.business.dto.TelefoneUpdateDTO;
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

    // ====================== NOVOS / ATUALIZADOS ======================

    @Transactional
    public UsuarioResponseDTO updateMe(UsuarioUpdateDTO dto) {
        String emailAtual = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(emailAtual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        // Verifica se o email está sendo alterado e se já existe
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
            throw new RuntimeException("Senha atual incorreta"); // TODO: Criar exceção específica depois
        }

        usuario.setSenha(passwordEncoder.encode(dto.getSenhaNova()));
        usuarioRepository.save(usuario);
    }

    @Transactional
    public EnderecoDTO atualizarEndereco(Long usuarioId, Long enderecoId, EnderecoUpdateDTO dto) {
        Enderecos endereco = enderecosRepository.findById(enderecoId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));

        validarPropriedadeDoEndereco(endereco, usuarioId);

        // Atualização completa
        endereco.setRua(dto.getRua());
        endereco.setNumero(dto.getNumero());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setCep(dto.getCep());

        Enderecos enderecoSalvo = enderecosRepository.save(endereco);

        return usuarioConverter.toEnderecoDTO(enderecoSalvo);   // ← Muito mais limpo
    }

    @Transactional
    public TelefoneDTO atualizarTelefone(Long usuarioId, Long telefoneId, TelefoneUpdateDTO dto) {
        Telefones telefone = telefonesRepository.findById(telefoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Telefone não encontrado"));

        validarPropriedadeDoTelefone(telefone, usuarioId);

        telefone.setDdd(dto.getDdd());
        telefone.setNumero(dto.getNumero());

        Telefones telefoneSalvo = telefonesRepository.save(telefone);

        return usuarioConverter.toTelefoneDTO(telefoneSalvo);   // ← Muito mais limpo
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