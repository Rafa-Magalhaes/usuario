package com.rafael.usuario.infrastructure.business.converter;

import com.rafael.usuario.infrastructure.business.dto.EnderecoDTO;
import com.rafael.usuario.infrastructure.business.dto.EnderecoUpdateDTO;
import com.rafael.usuario.infrastructure.business.dto.TelefoneDTO;
import com.rafael.usuario.infrastructure.business.dto.TelefoneUpdateDTO;
import com.rafael.usuario.infrastructure.business.dto.UsuarioDTO;
import com.rafael.usuario.infrastructure.business.dto.UsuarioResponseDTO;
import com.rafael.usuario.infrastructure.business.dto.UsuarioUpdateDTO;
import com.rafael.usuario.infrastructure.entity.Enderecos;
import com.rafael.usuario.infrastructure.entity.Telefones;
import com.rafael.usuario.infrastructure.entity.Usuario;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class UsuarioConverter {

    // ====================== DTO → Entity ======================
    public Usuario toEntity(UsuarioDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setNome(dto.getNome());
        usuario.setEmail(dto.getEmail());
        usuario.setSenha(dto.getSenha());

        if (dto.getEnderecos() != null) {
            List<Enderecos> enderecos = dto.getEnderecos().stream()
                    .map(enderecoDto -> toEnderecoEntity(enderecoDto, usuario))
                    .toList();
            usuario.setEnderecos(enderecos);
        }

        if (dto.getTelefones() != null) {
            List<Telefones> telefones = dto.getTelefones().stream()
                    .map(telefoneDto -> toTelefoneEntity(telefoneDto, usuario))
                    .toList();
            usuario.setTelefones(telefones);
        }

        return usuario;
    }

    public Enderecos toEnderecoEntity(EnderecoDTO dto, Usuario usuario) {
        Enderecos endereco = new Enderecos();
        endereco.setRua(dto.getRua());
        endereco.setNumero(dto.getNumero());
        endereco.setCep(dto.getCep());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        endereco.setUsuario(usuario);           // Vincula o endereço ao usuário
        return endereco;
    }

    public Telefones toTelefoneEntity(TelefoneDTO dto, Usuario usuario) {
        Telefones telefone = new Telefones();
        telefone.setDdd(dto.getDdd());
        telefone.setNumero(dto.getNumero());
        telefone.setUsuario(usuario);           // Vincula o telefone ao usuário
        return telefone;
    }

    // ====================== Entity → ResponseDTO ======================
    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getId(),
                usuario.getNome(),
                usuario.getEmail(),
                toEnderecoDTOList(usuario.getEnderecos()),
                toTelefoneDTOList(usuario.getTelefones())
        );
    }

    public EnderecoDTO toEnderecoDTO(Enderecos entity) {
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

    public TelefoneDTO toTelefoneDTO(Telefones entity) {
        TelefoneDTO dto = new TelefoneDTO();
        dto.setId(entity.getId());
        dto.setDdd(entity.getDdd());
        dto.setNumero(entity.getNumero());
        return dto;
    }

    private List<EnderecoDTO> toEnderecoDTOList(List<Enderecos> enderecos) {
        if (enderecos == null) return null;
        return enderecos.stream().map(this::toEnderecoDTO).toList();
    }

    private List<TelefoneDTO> toTelefoneDTOList(List<Telefones> telefones) {
        if (telefones == null) return null;
        return telefones.stream().map(this::toTelefoneDTO).toList();
    }

    // ====================== UPDATE METHODS ======================
    public void updateEntityFromDTO(Usuario usuario, UsuarioUpdateDTO dto) {
        if (dto.getNome() != null) usuario.setNome(dto.getNome());
        if (dto.getEmail() != null) usuario.setEmail(dto.getEmail());
    }

    public void updateEnderecoFromDTO(Enderecos endereco, EnderecoUpdateDTO dto) {
        if (dto.getRua() != null) endereco.setRua(dto.getRua());
        if (dto.getNumero() != null) endereco.setNumero(dto.getNumero());
        if (dto.getBairro() != null) endereco.setBairro(dto.getBairro());
        if (dto.getCidade() != null) endereco.setCidade(dto.getCidade());
        if (dto.getEstado() != null) endereco.setEstado(dto.getEstado());
        if (dto.getCep() != null) endereco.setCep(dto.getCep());
    }

    public void updateTelefoneFromDTO(Telefones telefone, TelefoneUpdateDTO dto) {
        if (dto.getDdd() != null) telefone.setDdd(dto.getDdd());
        if (dto.getNumero() != null) telefone.setNumero(dto.getNumero());
    }
}