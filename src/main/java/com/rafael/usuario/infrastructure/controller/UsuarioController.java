package com.rafael.usuario.infrastructure.controller;

import com.rafael.usuario.infrastructure.business.UsuarioService;
import com.rafael.usuario.infrastructure.business.dto.EnderecoDTO;
import com.rafael.usuario.infrastructure.business.dto.SenhaUpdateDTO;
import com.rafael.usuario.infrastructure.business.dto.TelefoneDTO;
import com.rafael.usuario.infrastructure.business.dto.UsuarioDTO;
import com.rafael.usuario.infrastructure.business.dto.UsuarioResponseDTO;
import com.rafael.usuario.infrastructure.business.dto.UsuarioUpdateDTO;
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

    // ==================== CADASTRO ====================
    @PostMapping
    public ResponseEntity<UsuarioResponseDTO> salvarUsuario(
            @RequestBody @Valid UsuarioDTO usuarioDTO) {

        UsuarioResponseDTO response = usuarioService.salvarUsuario(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== LOGIN ====================
    @PostMapping("/login")
    public String login(@RequestBody UsuarioDTO usuarioDTO) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        usuarioDTO.getEmail(),
                        usuarioDTO.getSenha()
                )
        );

        return "Bearer " + jwtUtil.generateToken(authentication.getName());
    }

    // ==================== BUSCAR ====================
    @GetMapping
    public ResponseEntity<UsuarioResponseDTO> buscarUsuarioPorEmail(
            @RequestParam("email") String email) {

        return ResponseEntity.ok(usuarioService.buscarUsuarioPorEmail(email));
    }

    // ==================== DELETAR ====================
    @DeleteMapping("/{email}")
    public ResponseEntity<Void> deletarUsuarioPorEmail(@PathVariable String email) {
        usuarioService.deletarUsuarioPorEmail(email);
        return ResponseEntity.noContent().build();
    }

    // ====================== ATUALIZAÇÕES ======================
    @PutMapping("/me")
    public ResponseEntity<UsuarioResponseDTO> atualizarMe(
            @Valid @RequestBody UsuarioUpdateDTO dto) {

        UsuarioResponseDTO response = usuarioService.updateMe(dto);
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