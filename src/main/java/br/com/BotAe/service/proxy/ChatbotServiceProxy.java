package br.com.BotAe.service.proxy;

import br.com.BotAe.model.Chatbot;
import br.com.BotAe.service.ChatbotService;
import org.apache.camel.ProducerTemplate;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class ChatbotServiceProxy implements ChatbotService {

    @Qualifier("chatbotServiceImpl")
    private final ChatbotService service;

    private final ProducerTemplate template;

    public ChatbotServiceProxy(
            @Qualifier("chatbotServiceImpl")
            ChatbotService service, ProducerTemplate template) {
        this.service = service;
        this.template = template;
    }

    @Override
    public Chatbot inserir(Chatbot chatbot) {
        Chatbot chatbotSalvo =  service.inserir(chatbot);

        template.requestBody("direct:nodejs-salvar", chatbotSalvo);
        return chatbotSalvo;
    }

    @Override
    public Chatbot atualizar(Chatbot chatbot) {
        Chatbot chatbotAlterado = service.atualizar(chatbot);

        template.requestBody("direct:nodejs-alterar", chatbotAlterado);
        return chatbotAlterado;
    }

    @Override
    public Page<Chatbot> listarTodos(Pageable page) {

        Page<Chatbot> chatbotsEncontrados = template.requestBody("direct:nodejs-listar", page, Page.class);

        if  (chatbotsEncontrados == null) {
            return service.listarTodos(page);
        }

        return chatbotsEncontrados;
    }

    @Override
    public Chatbot buscarPorNumero(String numero) {
        Chatbot chatbotEncontrado = service.buscarPorNumero(numero);

        template.requestBody("direct:nodejs-buscar", numero);
        return chatbotEncontrado;
    }

    @Override
    public void enviarMensagem(String numero, Map<String, Object> mensagem) {

        service.enviarMensagem(numero, mensagem);
        template.requestBody("direct:nodejs-enviar-mensagem", mensagem);
    }
}
