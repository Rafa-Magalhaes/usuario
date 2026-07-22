package com.rafael.usuario.domain.service;

import com.rafael.usuario.api.dto.* ;
import com.rafael.usuario.domain.entity.Endereco;
import com.rafael.usuario.domain.entity.Telefone;
import com.rafael.usuario.domain.entity.Usuario;
import com.rafael.usuario.domain.exceptions.ConflictException;
import com.rafael.usuario.domain.exceptions.RegraNegocioException;
import com.rafael.usuario.domain.exceptions.ResourceNotFoundException;
import com.rafael.usuario.infrastructure.mapper.UsuarioConverter;
import com.rafael.usuario.infrastructure.repository.EnderecoRepository;
import com.rafael.usuario.infrastructure.repository.TelefoneRepository;
import com.rafael.usuario.infrastructure.repository.UsuarioRepository;
import lombok.RequiredArgsConstructor;
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

    // ==================== DELETAR USUÁRIO ====================
    @Transactional
    public void deletarUsuario(String email) {
        usuarioRepository.deleteByEmail(email);
    }

    // ==================== ATUALIZAR NOME ====================
    @Transactional
    public void atualizarNome(String email, String novoNome) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));
        usuario.setNome(novoNome);
        usuarioRepository.save(usuario);
    }

    // ==================== ATUALIZAR SENHA ====================
    @Transactional
    public void atualizarSenha(BffUsuarioSetpassRequestDTO request, String email) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        if (!passwordEncoder.matches(request.getSenha(), usuario.getSenha())) {
            throw new RegraNegocioException("Senha atual incorreta");
        }

        usuario.setSenha(passwordEncoder.encode(request.getNovaSenha()));
        usuarioRepository.save(usuario);
    }

    // ==================== ATUALIZAR ENDEREÇO ====================
    @Transactional
    public void atualizarEndereco(String email, Long enderecoId, BffUsuarioEnderecoupdateRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));

        if (!endereco.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraNegocioException("Operação negada: Este endereço não pertence ao usuário informado.");
        }

        endereco.setRua(request.getRua());
        endereco.setNumero(request.getNumero());
        endereco.setCep(request.getCep());
        endereco.setBairro(request.getBairro());
        endereco.setCidade(request.getCidade());
        endereco.setEstado(request.getEstado());

        enderecoRepository.save(endereco);
    }

    // ==================== ATUALIZAR TELEFONE ====================
    @Transactional
    public void atualizarTelefone(String email, Long telefoneId, BffUsuarioTelefoneupdateRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Telefone telefone = telefoneRepository.findById(telefoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Telefone não encontrado"));

        if (!telefone.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraNegocioException("Operação negada: Este telefone não pertence ao usuário informado.");
        }

        telefone.setDdd(request.getDdd());
        telefone.setNumero(request.getNumero());

        telefoneRepository.save(telefone);
    }

    // ==================== DELETAR ENDEREÇO ====================
    @Transactional
    public void deletarEnderecoDefinitivo(String email, Long enderecoId) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Endereco endereco = enderecoRepository.findById(enderecoId)
                .orElseThrow(() -> new ResourceNotFoundException("Endereço não encontrado"));

        if (!endereco.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraNegocioException("Operação negada: Este endereço não pertence ao usuário informado.");
        }

        usuario.getEnderecos().remove(endereco);
        usuarioRepository.save(usuario);
    }

    // ==================== DELETAR TELEFONE ====================
    @Transactional
    public void deletarTelefoneDefinitivo(String email, Long telefoneId) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Telefone telefone = telefoneRepository.findById(telefoneId)
                .orElseThrow(() -> new ResourceNotFoundException("Telefone não encontrado"));

        if (!telefone.getUsuario().getId().equals(usuario.getId())) {
            throw new RegraNegocioException("Operação negada: Este telefone não pertence ao usuário informado.");
        }

        usuario.getTelefones().remove(telefone);
        usuarioRepository.save(usuario);
    }

    // ==================== ADICIONAR ENDEREÇO ====================
    @Transactional
    public EnderecoDTO adicionarEndereco(String email, BffUsuarioAddenderecoRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Endereco endereco = usuarioConverter.toEnderecoEntity(request, usuario);

        usuario.getEnderecos().add(endereco);
        Endereco enderecoSalvo = enderecoRepository.save(endereco);

        return usuarioConverter.toEnderecoDTO(enderecoSalvo);
    }

    // ==================== ADICIONAR TELEFONE ====================
    @Transactional
    public TelefoneDTO adicionarTelefone(String email, BffUsuarioAddtelefoneRequestDTO request) {
        Usuario usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuário não encontrado"));

        Telefone telefone = usuarioConverter.toTelefoneEntity(request, usuario);

        usuario.getTelefones().add(telefone);
        Telefone telefoneSalvo = telefoneRepository.save(telefone);

        return usuarioConverter.toTelefoneDTO(telefoneSalvo);
    }

}