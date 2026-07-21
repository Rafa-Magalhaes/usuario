package com.rafael.usuario.api.controller;

import com.rafael.usuario.api.dto.BffUsuarioEnderecoupdateRequestDTO;
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
            @Valid @RequestBody FrontUsuarioCadastroRequestDTO usuarioDTO) {

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

    // ==================== DELETAR USUÁRIO ====================
    @DeleteMapping("/internal/definitivo/{email}")
    public ResponseEntity<Void> deletarUsuario(@PathVariable String email) {
        usuarioService.deletarUsuario(email);
        return ResponseEntity.noContent().build();
    }

    // ==================== ATUALIZAR NOME ====================
    @PatchMapping("/internal/{email}/nome")
    public ResponseEntity<Void> atualizarNome(@PathVariable("email") String email, @RequestBody String novoNome) {
        usuarioService.atualizarNome(email, novoNome);
        return ResponseEntity.noContent().build();
    }

    // ==================== ATUALIZAR SENHA ====================
    @PatchMapping("/internal/{email}/senha")
    public ResponseEntity<Void> atualizarSenha(
            @PathVariable("email") String email,
            @Valid @RequestBody BffUsuarioSetpassRequestDTO request) {
        usuarioService.atualizarSenha(request, email);
        return ResponseEntity.noContent().build();
    }

    // ==================== ATUALIZAR ENDEREÇO ====================
    @PutMapping("/internal/{email}/enderecos/{enderecoId}")
    public ResponseEntity<Void> atualizarEndereco(
            @PathVariable("email") String email,
            @PathVariable("enderecoId") Long enderecoId,
            @Valid @RequestBody BffUsuarioEnderecoupdateRequestDTO request) {

        usuarioService.atualizarEndereco(email, enderecoId, request);
        return ResponseEntity.noContent().build();
    }

    // ==================== ATUALIZAR TELEFONE ====================
    @PutMapping("/internal/{email}/telefones/{telefoneId}")
    public ResponseEntity<Void> atualizarTelefone(
            @PathVariable("email") String email,
            @PathVariable("telefoneId") Long telefoneId,
            @Valid @RequestBody BffUsuarioTelefoneupdateRequestDTO request) {

        usuarioService.atualizarTelefone(email, telefoneId, request);
        return ResponseEntity.noContent().build();
    }

    // ==================== DELETAR ENDEREÇO ====================
    @DeleteMapping("/internal/definitivo/{email}/enderecos/{enderecoId}")
    public ResponseEntity<Void> deletarEnderecoDefinitivo(
            @PathVariable("email") String email,
            @PathVariable("enderecoId") Long enderecoId) {

        usuarioService.deletarEnderecoDefinitivo(email, enderecoId);
        return ResponseEntity.noContent().build();
    }

    // ==================== DELETAR TELEFONE ====================
    @DeleteMapping("/internal/definitivo/{email}/telefones/{telefoneId}")
    public ResponseEntity<Void> deletarTelefoneDefinitivo(
            @PathVariable("email") String email,
            @PathVariable("telefoneId") Long telefoneId) {

        usuarioService.deletarTelefoneDefinitivo(email, telefoneId);
        return ResponseEntity.noContent().build();
    }

    // ==================== ADICIONAR ENDEREÇO ====================
    @PostMapping("/internal/{email}/enderecos")
    public ResponseEntity<EnderecoDTO> adicionarEndereco(
            @PathVariable("email") String email,
            @Valid @RequestBody BffUsuarioAddenderecoRequestDTO request) {

        EnderecoDTO addEndereco = usuarioService.adicionarEndereco(email, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(addEndereco);
    }

    // ==================== ADICIONAR TELEFONE ====================
    @PostMapping("/internal/{email}/telefones")
    public ResponseEntity<TelefoneDTO> adicionarTelefone(
            @PathVariable("email") String email,
            @Valid @RequestBody BffUsuarioAddtelefoneRequestDTO request) {

        TelefoneDTO addTelefone = usuarioService.adicionarTelefone(email, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(addTelefone);
    }

}