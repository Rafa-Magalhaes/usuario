package com.rafael.usuario.api.controller;

import com.rafael.usuario.api.DTOaverificar.SenhaUpdateDTO;
import com.rafael.usuario.api.DTOaverificar.UsuarioUpdateDTO;
import com.rafael.usuario.api.dto.*;
import com.rafael.usuario.domain.service.UsuarioService;
import com.rafael.usuario.infrastructure.security.JwtUtil;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/usuarios")
@RequiredArgsConstructor
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // ==================== CADASTRAR NOVO USUÁRIO (ROTA EXTERNA)====================
    @PostMapping
    public ResponseEntity<UsuarioFrontCadastroResponseDTO> salvarUsuario(
            @RequestBody @Valid FrontUsuarioCadastroRequestDTO usuarioDTO) {

        UsuarioFrontCadastroResponseDTO response = usuarioService.salvarUsuario(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== LOGIN ====================
    @PostMapping("/login")
    public ResponseEntity<TokenResponseDTO> login(@RequestBody BffUsuarioLoginRequestDTO usuarioDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        usuarioDTO.getEmail(),
                        usuarioDTO.getSenha()
                )
        );

        String tokenGerado = jwtUtil.generateToken(authentication.getName());

        return ResponseEntity.ok(new TokenResponseDTO("Bearer " + tokenGerado));
    }

    // ====================== ENRIQUECER NOTIFICAÇÃO ======================
    @GetMapping("/internal/{usuarioId}")
    public ResponseEntity<UsuarioBffMailResponseDTO> buscarUsuarioInterno(@PathVariable Long usuarioId) {
        UsuarioBffMailResponseDTO usuario = usuarioService.buscarUsuarioPorIdInterno(usuarioId);
        return ResponseEntity.ok(usuario);
    }

    // ==================== BUSCAR PERFIL============================
    @GetMapping("/internal/perfil/{email}")
    public ResponseEntity<UsuarioBffPerfilResponseDTO> buscarUsuarioPorEmail(
            @PathVariable("email") String email) {

        return ResponseEntity.ok(usuarioService.buscarUsuarioPorEmail(email));
    }

    // ==================== BUSCAR PERFIL, RETORNA SOMENTE ID====================
    @GetMapping("/internal/id/{email}")
    public ResponseEntity<Long> buscarIdPorEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(usuarioService.buscarIdPorEmail(email));
    }





    // ==================== DELETAR ====================
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deletarUsuarioPorEmail(@PathVariable String email) {
        usuarioService.deletarUsuarioPorEmail(email);
        return ResponseEntity.noContent().build();
    }

    // ====================== ATUALIZAÇÕES ======================
    @PutMapping("/me")
    public ResponseEntity<UsuarioFrontCadastroResponseDTO> atualizarMe(
            @Valid @RequestBody UsuarioUpdateDTO dto) {

        UsuarioFrontCadastroResponseDTO response = usuarioService.updateMe(dto);
        return ResponseEntity.ok(response);
    }

    @PutMapping("/me/senha")
    public ResponseEntity<Void> atualizarSenha(
            @Valid @RequestBody SenhaUpdateDTO dto) {

        usuarioService.atualizarSenha(dto);
        return ResponseEntity.noContent().build();
    }

    // ====================== ADIÇÃO DE ENDEREÇOS E TELEFONES ======================

    @PostMapping("/{usuarioId}/enderecos")
    public ResponseEntity<EnderecoDTO> adicionarEndereco(
            @PathVariable Long usuarioId,
            @Valid @RequestBody EnderecoDTO dto) {

        EnderecoDTO response = usuarioService.adicionarEndereco(usuarioId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @PostMapping("/{usuarioId}/telefones")
    public ResponseEntity<TelefoneDTO> adicionarTelefone(
            @PathVariable Long usuarioId,
            @Valid @RequestBody TelefoneDTO dto) {

        TelefoneDTO response = usuarioService.adicionarTelefone(usuarioId, dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}