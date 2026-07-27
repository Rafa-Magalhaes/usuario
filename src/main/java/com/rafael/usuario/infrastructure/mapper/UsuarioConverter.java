package com.rafael.usuario.infrastructure.mapper;

import com.rafael.usuario.api.dto.*;
import com.rafael.usuario.domain.entity.Endereco;
import com.rafael.usuario.domain.entity.Telefone;
import com.rafael.usuario.domain.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsuarioConverter {

    // ====================== CADASTRO DE NOVO USUÁRIO ======================
    public Usuario toEntity(FrontUsuarioCadastroRequestDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());

        if (dto.getEnderecos() != null) {
            List<Endereco> enderecos = dto.getEnderecos().stream()
                    .map(enderecoDto -> toEnderecoEntity(enderecoDto, usuario))
                    .toList();
            usuario.setEnderecos(enderecos);
        }

        if (dto.getTelefones() != null) {
            List<Telefone> telefones = dto.getTelefones().stream()
                    .map(telefoneDto -> toTelefoneEntity(telefoneDto, usuario))
                    .toList();
            usuario.setTelefones(telefones);
        }

        return usuario;
    }

    public Endereco toEnderecoEntity(EnderecoDTO dto, Usuario usuario) {
        Endereco endereco = new Endereco();
        endereco.setRua(dto.getRua());
        endereco.setNumero(dto.getNumero());
        endereco.setCep(dto.getCep());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setUsuario(usuario);
        return endereco;
    }

    public Telefone toTelefoneEntity(TelefoneDTO dto, Usuario usuario) {
        Telefone telefone = new Telefone();
        telefone.setDdd(dto.getDdd());
        telefone.setNumero(dto.getNumero());
        telefone.setUsuario(usuario);
        return telefone;
    }

    public UsuarioFrontCadastroResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioFrontCadastroResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                toEnderecoDTOList(usuario.getEnderecos()),
                toTelefoneDTOList(usuario.getTelefones())
        );
    }

    // ====================== ENRIQUECIMENTO PARA DISPARO DE E-MAIL ======================
    public UsuarioBffMailResponseDTO toEnriquecimentoDTO(Usuario usuario) {
        return UsuarioBffMailResponseDTO.builder()
                .email(usuario.getEmail())
                .nome(usuario.getNome())
                .build();
    }

    // ==================== BUSCA PERFIL (ROTA INTERNA) ====================
    public UsuarioBffPerfilResponseDTO toInternalResponseDTO(Usuario usuario) {
        return new UsuarioBffPerfilResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                toEnderecoDTOList(usuario.getEnderecos()),
                toTelefoneDTOList(usuario.getTelefones())
        );
    }

    private List<EnderecoDTO> toEnderecoDTOList(List<Endereco> enderecos) {
        if (enderecos == null) return null;
        return enderecos.stream().map(this::toEnderecoDTO).toList();
    }

    private List<TelefoneDTO> toTelefoneDTOList(List<Telefone> telefones) {
        if (telefones == null) return null;
        return telefones.stream().map(this::toTelefoneDTO).toList();
    }

    public EnderecoDTO toEnderecoDTO(Endereco entity) {
        EnderecoDTO dto = new EnderecoDTO();
        dto.setId(entity.getId());
        dto.setRua(entity.getRua());
        dto.setNumero(entity.getNumero());
        dto.setCep(entity.getCep());
        dto.setBairro(entity.getBairro());
        dto.setCidade(entity.getCidade());
        dto.setEstado(entity.getEstado());
        return dto;
    }

    public TelefoneDTO toTelefoneDTO(Telefone entity) {
        TelefoneDTO dto = new TelefoneDTO();
        dto.setId(entity.getId());
        dto.setDdd(entity.getDdd());
        dto.setNumero(entity.getNumero());
        return dto;
    }

    // ==================== ADICIONAR ENDERECO ====================
//  (Usado exclusivamente para a Rota de Adicionar Endereço)
    public Endereco toEnderecoEntity(BffUsuarioAddenderecoRequestDTO request, Usuario usuario) {
        Endereco endereco = new Endereco();
        endereco.setRua(request.getRua());
        endereco.setNumero(request.getNumero());
        endereco.setCep(request.getCep());
        endereco.setBairro(request.getBairro());
        endereco.setCidade(request.getCidade());
        endereco.setEstado(request.getEstado());
        endereco.setUsuario(usuario);
        return endereco;
    }

    // ==================== ADICIONAR TELEFONE ====================
//  (Usado exclusivamente para a Rota de Adicionar Telefone)
    public Telefone toTelefoneEntity(BffUsuarioAddtelefoneRequestDTO request, Usuario usuario) {
        Telefone telefone = new Telefone();
        telefone.setDdd(request.getDdd());
        telefone.setNumero(request.getNumero());
        telefone.setUsuario(usuario);
        return telefone;
    }
}

