package br.com.BotAe.service;

import br.com.BotAe.model.Chatbot;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Validated
public interface ChatbotService {

    Chatbot inserir(Chatbot chatbot);

    Chatbot atualizar(Chatbot chatbot);

    Page<Chatbot> listarTodos(Pageable page);

    Chatbot buscarPorNumero(String numero);

    void enviarMensagem(
            @NotBlank(message = "O número é obrigatório")
            String numero,
            @NotBlank(message = "O corpo da mensagem é obrigatório")
            Map<String, Object> mensagem);
}
