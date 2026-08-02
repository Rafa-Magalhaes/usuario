package com.rafael.usuario.api.controller;

import com.rafael.usuario.api.dto.BffUsuarioEnderecoupdateRequestDTO;
import com.rafael.usuario.api.dto.*;
import com.rafael.usuario.domain.service.UsuarioService;
import com.rafael.usuario.infrastructure.security.JwtUtil;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "Usuários (Core Domain)", description = "Endpoints de gerenciamento de contas, perfis, sub-recursos e autenticação relacional")
public class UsuarioController {

    private final UsuarioService usuarioService;
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;

    // ==================== BUSCAR PERFIL, RETORNA SOMENTE ID====================
    @GetMapping("/internal/id/{email}")
    @Operation(summary = "Busca o ID do usuário pelo e-mail", description = "Endpoint interno M2M utilizado pelo BFF para traduzir o e-mail do token em ID numérico.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "ID retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Long> buscarIdPorEmail(@PathVariable("email") String email) {
        return ResponseEntity.ok(usuarioService.buscarIdPorEmail(email));
    }

    // ==================== BUSCAR PERFIL============================
    @GetMapping("/internal/perfil/{email}")
    @Operation(summary = "Busca o perfil completo do usuário", description = "Endpoint interno M2M que retorna dados cadastrais, endereços e telefones.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Perfil retornado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UsuarioBffPerfilResponseDTO> buscarUsuarioPorEmail(
            @PathVariable("email") String email) {

        return ResponseEntity.ok(usuarioService.buscarUsuarioPorEmail(email));
    }

    // ==================== CADASTRAR NOVO USUÁRIO (ROTA EXTERNA)====================
    @PostMapping
    @Operation(summary = "Cadastra um novo usuário", description = "Rota pública de auto-cadastro. Criptografa a senha e persiste o usuário com endereços e telefones em cascata.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Usuário cadastrado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Erro de validação nos campos (Bean Validation)"),
            @ApiResponse(responseCode = "409", description = "E-mail já cadastrado no sistema")
    })
    public ResponseEntity<UsuarioFrontCadastroResponseDTO> salvarUsuario(
            @Valid @RequestBody FrontUsuarioCadastroRequestDTO usuarioDTO) {

        UsuarioFrontCadastroResponseDTO response = usuarioService.salvarUsuario(usuarioDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // ==================== LOGIN ====================
    @PostMapping("/internal/login")
    @Operation(summary = "Autentica o usuário e emite o JWT", description = "Rota pública de login. Valida credenciais via Spring Security e retorna o token de acesso.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Autenticação bem-sucedida e token retornado"),
            @ApiResponse(responseCode = "401", description = "Credenciais inválidas")
    })
    public ResponseEntity<TokenResponseDTO> login(@Valid @RequestBody BffUsuarioLoginRequestDTO usuarioDTO) {
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
    @Operation(summary = "Busca dados básicos para enriquecimento de e-mail", description = "Endpoint interno M2M para resgatar nome e e-mail pelo ID.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Dados retornados com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<UsuarioBffMailResponseDTO> buscarUsuarioInterno(@PathVariable Long usuarioId) {
        UsuarioBffMailResponseDTO usuario = usuarioService.buscarUsuarioPorIdInterno(usuarioId);
        return ResponseEntity.ok(usuario);
    }

    // ==================== DELETAR USUÁRIO ====================
    @DeleteMapping("/internal/definitivo/{email}")
    @Operation(summary = "Deleta o usuário em definitivo", description = "Endpoint interno M2M que remove o usuário e todas as suas dependências do banco relacional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Usuário deletado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> deletarUsuario(@PathVariable String email) {
        usuarioService.deletarUsuario(email);
        return ResponseEntity.noContent().build();
    }

    // ==================== ATUALIZAR NOME ====================
    @PatchMapping("/internal/{email}/nome")
    @Operation(summary = "Atualiza o nome do usuário", description = "Endpoint interno protegido por Token de Serviço.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Nome atualizado com sucesso"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> atualizarNome(
            @PathVariable("email") String email,
            @Valid @RequestBody BffUsuarioSetnameRequestDTO request) {

        usuarioService.atualizarNome(email, request.getNome());
        return ResponseEntity.noContent().build();
    }

    // ==================== ATUALIZAR SENHA ====================
    @PatchMapping("/internal/{email}/senha")
    @Operation(summary = "Atualiza a senha do usuário", description = "Valida a senha atual informada, criptografa a nova senha e persiste.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Senha atualizada com sucesso"),
            @ApiResponse(responseCode = "400", description = "Senha atual incorreta ou dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<Void> atualizarSenha(
            @PathVariable("email") String email,
            @Valid @RequestBody BffUsuarioSetpassRequestDTO request) {
        usuarioService.atualizarSenha(request, email);
        return ResponseEntity.noContent().build();
    }

    // ==================== ATUALIZAR ENDEREÇO ====================
    @PutMapping("/internal/{email}/enderecos/{enderecoId}")
    @Operation(summary = "Atualiza um endereço", description = "Executa validação de ownership (pertencimento) antes de persistir as alterações.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Endereço atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Violação de regra de negócio (endereço não pertence ao usuário)"),
            @ApiResponse(responseCode = "404", description = "Usuário ou endereço não encontrado")
    })
    public ResponseEntity<Void> atualizarEndereco(
            @PathVariable("email") String email,
            @PathVariable("enderecoId") Long enderecoId,
            @Valid @RequestBody BffUsuarioEnderecoupdateRequestDTO request) {

        usuarioService.atualizarEndereco(email, enderecoId, request);
        return ResponseEntity.noContent().build();
    }

    // ==================== ATUALIZAR TELEFONE ====================
    @PutMapping("/internal/{email}/telefones/{telefoneId}")
    @Operation(summary = "Atualiza um telefone", description = "Executa validação de ownership (pertencimento) antes de atualizar o registro.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Telefone atualizado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Violação de regra de negócio (telefone não pertence ao usuário)"),
            @ApiResponse(responseCode = "404", description = "Usuário ou telefone não encontrado")
    })
    public ResponseEntity<Void> atualizarTelefone(
            @PathVariable("email") String email,
            @PathVariable("telefoneId") Long telefoneId,
            @Valid @RequestBody BffUsuarioTelefoneupdateRequestDTO request) {

        usuarioService.atualizarTelefone(email, telefoneId, request);
        return ResponseEntity.noContent().build();
    }

    // ==================== DELETAR ENDEREÇO ====================
    @DeleteMapping("/internal/definitivo/{email}/enderecos/{enderecoId}")
    @Operation(summary = "Remove um endereço do usuário", description = "Valida a posse e remove o endereço utilizando orphanRemoval do JPA.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Endereço removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Violação de regra de propriedade"),
            @ApiResponse(responseCode = "404", description = "Usuário ou endereço não encontrado")
    })
    public ResponseEntity<Void> deletarEnderecoDefinitivo(
            @PathVariable("email") String email,
            @PathVariable("enderecoId") Long enderecoId) {

        usuarioService.deletarEnderecoDefinitivo(email, enderecoId);
        return ResponseEntity.noContent().build();
    }

    // ==================== DELETAR TELEFONE ====================
    @DeleteMapping("/internal/definitivo/{email}/telefones/{telefoneId}")
    @Operation(summary = "Remove um telefone do usuário", description = "Valida a posse e remove o telefone da base relacional.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Telefone removido com sucesso"),
            @ApiResponse(responseCode = "400", description = "Violação de regra de propriedade"),
            @ApiResponse(responseCode = "404", description = "Usuário ou telefone não encontrado")
    })
    public ResponseEntity<Void> deletarTelefoneDefinitivo(
            @PathVariable("email") String email,
            @PathVariable("telefoneId") Long telefoneId) {

        usuarioService.deletarTelefoneDefinitivo(email, telefoneId);
        return ResponseEntity.noContent().build();
    }

    // ==================== ADICIONAR ENDEREÇO ====================
    @PostMapping("/internal/{email}/enderecos")
    @Operation(summary = "Adiciona um novo endereço ao usuário", description = "Vincula um novo registro de endereço à entidade pai.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Endereço criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<EnderecoDTO> adicionarEndereco(
            @PathVariable("email") String email,
            @Valid @RequestBody BffUsuarioAddenderecoRequestDTO request) {

        EnderecoDTO addEndereco = usuarioService.adicionarEndereco(email, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(addEndereco);
    }

    // ==================== ADICIONAR TELEFONE ====================
    @PostMapping("/internal/{email}/telefones")
    @Operation(summary = "Adiciona um novo telefone ao usuário", description = "Vincula um novo registro de telefone à entidade pai.")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Telefone criado com sucesso"),
            @ApiResponse(responseCode = "400", description = "Dados inválidos"),
            @ApiResponse(responseCode = "404", description = "Usuário não encontrado")
    })
    public ResponseEntity<TelefoneDTO> adicionarTelefone(
            @PathVariable("email") String email,
            @Valid @RequestBody BffUsuarioAddtelefoneRequestDTO request) {

        TelefoneDTO addTelefone = usuarioService.adicionarTelefone(email, request);

        return ResponseEntity.status(HttpStatus.CREATED).body(addTelefone);
    }

}