package com.rafael.usuario.infrastructure.mapper;

import com.rafael.usuario.api.dto.*;
import com.rafael.usuario.api.DTOaverificar.EnderecoUpdateDTO;
import com.rafael.usuario.api.DTOaverificar.TelefoneUpdateDTO;
import com.rafael.usuario.api.DTOaverificar.UsuarioUpdateDTO;
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
        endereco.setUsuario(usuario);           // Vincula o endereço ao usuário
        return endereco;
    }

    public Telefone toTelefoneEntity(TelefoneDTO dto, Usuario usuario) {
        Telefone telefone = new Telefone();
        telefone.setDdd(dto.getDdd());
        telefone.setNumero(dto.getNumero());
        telefone.setUsuario(usuario);           // Vincula o telefone ao usuário
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
        return new UsuarioBffMailResponseDTO(
                usuario.getNome(),
                usuario.getEmail()
        );
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

    private List<EnderecoDTO> toEnderecoDTOList(List<Endereco> enderecos) {
        if (enderecos == null) return null;
        return enderecos.stream().map(this::toEnderecoDTO).toList();
    }

    private List<TelefoneDTO> toTelefoneDTOList(List<Telefone> telefones) {
        if (telefones == null) return null;
        return telefones.stream().map(this::toTelefoneDTO).toList();
    }

    // ====================== UPDATE METHODS ======================
    public void updateEntityFromDTO(Usuario usuario, UsuarioUpdateDTO dto) {
        if (dto.getNome() != null) usuario.setNome(dto.getNome());
        if (dto.getEmail() != null) usuario.setEmail(dto.getEmail());
    }

    public void updateEnderecoFromDTO(Endereco endereco, EnderecoUpdateDTO dto) {
        if (dto.getRua() != null) endereco.setRua(dto.getRua());
        if (dto.getNumero() != null) endereco.setNumero(dto.getNumero());
        if (dto.getBairro() != null) endereco.setBairro(dto.getBairro());
        if (dto.getCidade() != null) endereco.setCidade(dto.getCidade());
        if (dto.getEstado() != null) endereco.setEstado(dto.getEstado());
        if (dto.getCep() != null) endereco.setCep(dto.getCep());
    }

    public void updateTelefoneFromDTO(Telefone telefone, TelefoneUpdateDTO dto) {
        if (dto.getDdd() != null) telefone.setDdd(dto.getDdd());
        if (dto.getNumero() != null) telefone.setNumero(dto.getNumero());
    }
}