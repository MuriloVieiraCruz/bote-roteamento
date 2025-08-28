package br.com.BotAe.controller;

import br.com.BotAe.config.MapConverter;
import br.com.BotAe.model.Chatbot;
import br.com.BotAe.service.ChatbotService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.net.URI;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/chatbot")
@Tag(name = "Chatbot", description = "Endpoints para gerenciamento de chatbots")
@SecurityRequirement(name = "bearerAuth")
public class ChatbotController {

    @Qualifier("chatbotServiceProxy")
    private final ChatbotService chatbotService;

    private final MapConverter converter;

    public ChatbotController(
            @Qualifier("chatbotServiceProxy")
            ChatbotService chatbotService, MapConverter converter) {
        this.chatbotService = chatbotService;
        this.converter = converter;
    }

    @PostMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Inserir chatbot", description = "Cria um novo chatbot no sistema")
    public ResponseEntity<Map<String, Object>> inserir(@RequestBody Chatbot chatbot) {
        Chatbot chatbotCriado = chatbotService.inserir(chatbot);

        return ResponseEntity.created(URI.create("/api/chatbot/" + chatbot.getNumero()))
                .body(converter.toJsonMap(chatbotCriado));
    }

    @PutMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Atualizar chatbot", description = "Atualiza um chatbot existente")
    public ResponseEntity<Map<String, Object>> atualizar(@RequestBody Chatbot chatbot) {
        Chatbot chatbotAtualizado = chatbotService.atualizar(chatbot);
        return ResponseEntity.ok().body(converter.toJsonMap(chatbotAtualizado));
    }

    @GetMapping
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Listar chatbots", description = "Lista todos os chatbots do sistema")
    public ResponseEntity<Map<String, Object>> listarTodos(
            @RequestParam Optional<Integer> pagina,
            @RequestParam Optional<Integer> tamanho) {

        Pageable page = PageRequest.of(pagina.orElse(0), tamanho.orElse(20));
        Page<Chatbot> chatbots = chatbotService.listarTodos(page);

        return ResponseEntity.ok().body(converter.toJsonList(chatbots));
    }

    @GetMapping("/{numero}")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Buscar chatbot", description = "Busca um chatbot específico pelo número")
    public ResponseEntity<Object> buscarPorNumero(@PathVariable String numero) {
        Chatbot chatbot = chatbotService.buscarPorNumero(numero);

        return ResponseEntity.ok().body(converter.toJsonMap(chatbot));
    }

    @PostMapping("/{numero}/envio")
    @PreAuthorize("hasRole('USER')")
    @Operation(summary = "Enviar mensagem", description = "Envia uma mensagem através de um chatbot específico")
    public ResponseEntity<Map<String, Object>> enviarMensagem(
            @PathVariable String numero,
            @RequestBody Map<String, Object> mensagemData) {

        chatbotService.enviarMensagem(numero, mensagemData);
        return ResponseEntity.ok().build();
    }
}
