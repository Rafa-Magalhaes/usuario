package com.rafael.usuario.infrastructure.business.converter;

import infrastructure.business.dto.UsuarioUpdateDTO;
import com.rafael.usuario.infrastructure.entity.Usuario;
import org.springframework.stereotype.Component;
import com.rafael.usuario.infrastructure.business.dto.EnderecoDTO;
import com.rafael.usuario.infrastructure.business.dto.TelefoneDTO;
import com.rafael.usuario.infrastructure.business.dto.UsuarioDTO;
import com.rafael.usuario.infrastructure.business.dto.UsuarioResponseDTO;
import com.rafael.usuario.infrastructure.entity.Enderecos;
import com.rafael.usuario.infrastructure.entity.Telefones;



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
                    .map(this::toEnderecoEntity)
                    .toList();
            usuario.setEnderecos(enderecos);
        }

        if (dto.getTelefones() != null) {
            List<Telefones> telefones = dto.getTelefones().stream()
                    .map(this::toTelefoneEntity)
                    .toList();
            usuario.setTelefones(telefones);
        }

        return usuario;
    }

    private Enderecos toEnderecoEntity(EnderecoDTO dto) {
        Enderecos endereco = new Enderecos();
        endereco.setRua(dto.getRua());
        endereco.setNumero(dto.getNumero());
        endereco.setCep(dto.getCep());
        endereco.setBairro(dto.getBairro());
        endereco.setCidade(dto.getCidade());
        endereco.setEstado(dto.getEstado());
        return endereco;
    }

    private Telefones toTelefoneEntity(TelefoneDTO dto) {
        Telefones telefone = new Telefones();
        telefone.setDdd(dto.getDdd());
        telefone.setNumero(dto.getNumero());
        return telefone;
    }

    // ====================== Entity → ResponseDTO ======================
    public UsuarioResponseDTO toResponseDTO(Usuario usuario) {
        return new UsuarioResponseDTO(
                usuario.getNome(),
                usuario.getEmail(),
                toEnderecoDTOList(usuario.getEnderecos()),
                toTelefoneDTOList(usuario.getTelefones())
        );
    }

    private List<EnderecoDTO> toEnderecoDTOList(List<Enderecos> enderecos) {
        if (enderecos == null) return null;
        return enderecos.stream().map(this::toEnderecoDTO).toList();
    }

    private List<TelefoneDTO> toTelefoneDTOList(List<Telefones> telefones) {
        if (telefones == null) return null;
        return telefones.stream().map(this::toTelefoneDTO).toList();
    }

    private EnderecoDTO toEnderecoDTO(Enderecos entity) {
        return new EnderecoDTO(
                entity.getRua(),
                entity.getNumero(),
                entity.getCep(),
                entity.getBairro(),
                entity.getCidade(),
                entity.getEstado()
        );
    }

    private TelefoneDTO toTelefoneDTO(Telefones entity) {
        return new TelefoneDTO(entity.getDdd(), entity.getNumero());
    }

        // ... seus métodos existentes (toEntity, toResponseDTO, etc.)

        /**
         * Aplica atualização parcial no usuário (merge)
         */
        public void updateEntityFromDTO(Usuario usuario, UsuarioUpdateDTO dto) {
            if (dto.getNome() != null) {
                usuario.setNome(dto.getNome());
            }
            if (dto.getEmail() != null) {
                usuario.setEmail(dto.getEmail());
            }
            // Senha NUNCA é alterada aqui (fica no Service por causa do BCrypt)
        }

}