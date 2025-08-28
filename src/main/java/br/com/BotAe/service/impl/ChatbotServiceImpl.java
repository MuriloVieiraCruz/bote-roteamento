package br.com.BotAe.service.impl;

import br.com.BotAe.model.Chatbot;
import br.com.BotAe.repository.ChatbotRepository;
import br.com.BotAe.service.ChatbotService;
import com.google.common.base.Preconditions;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChatbotServiceImpl implements ChatbotService {

    private final ChatbotServiceImpl self;
    private final ChatbotRepository chatbotRepository;

    public ChatbotServiceImpl(ChatbotServiceImpl self, ChatbotRepository chatbotRepository) {
        this.self = self;
        this.chatbotRepository = chatbotRepository;
    }

    @Override
    public Chatbot inserir(Chatbot chatbot) {

        this.validar(chatbot);
        return chatbotRepository.save(chatbot);
    }

    @Override
    public Chatbot atualizar(Chatbot chatbot) {
        self.buscarPorNumero(chatbot.getNumero());

        return chatbotRepository.save(chatbot);
    }

    @Override
    @Cacheable(value = "chatbots", key = "'all'")
    public Page<Chatbot> listarTodos(Pageable page) {
        return chatbotRepository.findAll(page);
    }

    @Override
    @Cacheable(value = "chatbots", key = "#numero")
    public Chatbot buscarPorNumero(String numero) {
        return chatbotRepository.findByNumero(numero).orElseThrow(() ->
                new IllegalArgumentException("Não foi encontrado um bot para o número: " + numero));
    }

    @Override
    public void enviarMensagem(String numero, Map<String, Object> mensagem) {

    }

    private void validar(Chatbot chatbot) {
        Preconditions.checkArgument(!chatbotRepository.existsById(chatbot.getNumero()),
                "Já existe um chatbot com o número: " + chatbot.getNumero());
    }
}
