package com.rafael.usuario.domain.service;

import com.rafael.usuario.api.DTOaverificar.SenhaUpdateDTO;
import com.rafael.usuario.api.DTOaverificar.UsuarioUpdateDTO;
import com.rafael.usuario.api.dto.* ;
import com.rafael.usuario.domain.entity.Endereco;
import com.rafael.usuario.domain.entity.Telefone;
import com.rafael.usuario.domain.entity.Usuario;
import com.rafael.usuario.infrastructure.exceptions.ConflictException;
import com.rafael.usuario.infrastructure.exceptions.ResourceNotFoundException;
import com.rafael.usuario.infrastructure.mapper.UsuarioConverter;
import com.rafael.usuario.infrastructure.repository.EnderecoRepository;
import com.rafael.usuario.infrastructure.repository.TelefoneRepository;
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
    private final EnderecoRepository enderecoRepository;
    private final TelefoneRepository telefoneRepository;
    private final UsuarioConverter usuarioConverter;
    private final PasswordEncoder passwordEncoder;

    // ==================== CADASTRAR NOVO USUÁRIO (ROTA EXTERNA)====================
    @Transactional
    public UsuarioFrontCadastroResponseDTO salvarUsuario(FrontUsuarioCadastroRequestDTO usuarioDTO) {
        if (usuarioRepository.existsByEmail(usuarioDTO.getEmail())) {
            throw new ConflictException("Já existe um usuário cadastrado com o email: " + usuarioDTO.getEmail());
        }

        Usuario usuario = usuarioConverter.toEntity(usuarioDTO);
        usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));

        Usuario usuarioSalvo = usuarioRepository.save(usuario);
        return usuarioConverter.toResponseDTO(usuarioSalvo);
    }

    // ====================== ENRIQUECER NOTIFICAÇÃO ======================

    public UsuarioBffMailResponseDTO buscarUsuarioPorIdInterno(Long UsuarioId) {
        Usuario usuario = usuarioRepository.findById(UsuarioId)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o id: " + UsuarioId));

        return usuarioConverter.toEnriquecimentoDTO(usuario);
    }

    // ==================== BUSCAR PERFIL, RETORNA CADASTRO============================
    public UsuarioBffPerfilResponseDTO buscarUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o email: " + email));

        return usuarioConverter.toInternalResponseDTO(usuario);
    }

    // ==================== BUSCAR PERFIL, RETORNA SOMENTE ID====================
    public Long buscarIdPorEmail(String email) {
        return usuarioRepository.findIdByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o email: " + email));
    }




    @Transactional
    public void deletarUsuarioPorEmail(String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado com o email: " + email));

        usuarioRepository.delete(usuario);
    }

    @Transactional
    public UsuarioFrontCadastroResponseDTO updateMe(UsuarioUpdateDTO dto) {
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

    // ====================== ADIÇÃO DE ENDEREÇO E TELEFONE ======================

    @Transactional
    public EnderecoDTO adicionarEndereco(Long usuarioId, EnderecoDTO dto) {
        String emailAtual = SecurityContextHolder.getContext().getAuthentication().getName();

        Usuario usuario = usuarioRepository.findByEmail(emailAtual)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!usuario.getId().equals(usuarioId)) {
            throw new ResourceNotFoundException("Você não tem permissão para adicionar endereço neste usuário");
        }

        Endereco endereco = usuarioConverter.toEnderecoEntity(dto, usuario);
        Endereco enderecoSalvo = enderecoRepository.save(endereco);

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

        Telefone telefone = usuarioConverter.toTelefoneEntity(dto, usuario);
        Telefone telefoneSalvo = telefoneRepository.save(telefone);

        return usuarioConverter.toTelefoneDTO(telefoneSalvo);
    }

    // ====================== MÉTODOS PRIVADOS ======================

    private void validarPropriedadeDoEndereco(Endereco endereco, Long usuarioId) {
        if (endereco.getUsuario() == null || !endereco.getUsuario().getId().equals(usuarioId)) {
            throw new ResourceNotFoundException("Endereço não pertence ao usuário informado");
        }
    }

    private void validarPropriedadeDoTelefone(Telefone telefone, Long usuarioId) {
        if (telefone.getUsuario() == null || !telefone.getUsuario().getId().equals(usuarioId)) {
            throw new ResourceNotFoundException("Telefone não pertence ao usuário informado");
        }
    }

}