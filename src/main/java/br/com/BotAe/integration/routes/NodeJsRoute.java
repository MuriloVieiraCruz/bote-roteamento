package br.com.BotAe.integration.routes;

import org.apache.camel.Exchange;
import org.apache.camel.LoggingLevel;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class NodeJsRoute extends RouteBuilder {

    @Value("${nodejs.base-url:http://localhost:3030}")
    private String nodeJsBaseUrl;

    @Override
    public void configure() {

        onException(Exception.class)
                .handled(true)
                .log(LoggingLevel.ERROR, "Erro na integração com Node.js. Erro: ${exception.stacktrace}")
                .setProperty("error", simple("${exception}"))
                .process(exchange -> {
                    Exception exception = exchange.getProperty("error", Exception.class);
                    exchange.getIn().setBody("Erro ao conectar com Node.js: " + exception.getMessage());
                    exchange.getIn().setHeader("CamelHttpResponseCode", 500);
                })
                .end();

        from("direct:nodejs-salvar")
                .routeId("nodejs-inserir-chatbot")
                .log("Iniciando inserção de chatbot no Node.js. Número: ${body.numero}")
                .process(exchange -> {
                    Object chatbot = exchange.getIn().getBody();
                    if (chatbot == null) {
                        throw new IllegalArgumentException("Chatbot não pode estar vazio");
                    }

                    exchange.getIn().setHeader("Content-Type", "application/json");
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
                })
                .marshal().json()
                .log("Enviando requisição POST para Node.js...")
                .to(nodeJsBaseUrl + "/api/chatbot")
                .log("Chatbot inserido com sucesso no Node.js. Resposta: ${body}")
                .unmarshal().json();

        from("direct:nodejs-alterar")
                .routeId("nodejs-atualizar-chatbot")
                .log("Iniciando atualização de chatbot no Node.js. Número: ${body.numero}")
                .process(exchange -> {
                    Object chatbot = exchange.getIn().getBody();
                    if (chatbot == null) {
                        throw new IllegalArgumentException("Chatbot não pode estar vazio");
                    }

                    exchange.getIn().setHeader("Content-Type", "application/json");
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "PUT");
                })
                .marshal().json()
                .log("Enviando requisição PUT para Node.js...")
                .to(nodeJsBaseUrl + "/api/chatbot")
                .log("Chatbot atualizado com sucesso no Node.js. Resposta: ${body}")
                .unmarshal().json();

        from("direct:nodejs-buscar")
                .routeId("nodejs-buscar-chatbot")
                .log("Iniciando busca de chatbot no Node.js. Número: ${body}")
                .process(exchange -> {
                    String numero = exchange.getIn().getBody(String.class);
                    if (numero == null || numero.trim().isEmpty()) {
                        throw new IllegalArgumentException("Número do chatbot é obrigatório");
                    }

                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
                    exchange.getIn().setHeader("numero", numero);
                    exchange.getIn().setBody(null);
                })
                .log("Enviando requisição GET para Node.js...")
                .toD(nodeJsBaseUrl + "/api/chatbot/${header.numero}")
                .log("Chatbot encontrado no Node.js. Resposta: ${body}")
                .unmarshal().json();

        from("direct:nodejs-listar")
                .routeId("nodejs-listar-chatbots")
                .log("Iniciando listagem de chatbots no Node.js")
                .process(exchange -> {
                    Pageable pageable = exchange.getIn().getBody(Pageable.class);

                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "GET");
                    exchange.getIn().setBody(null);

                    if (pageable != null) {
                        exchange.getIn().setHeader("page", pageable.getPageNumber());
                        exchange.getIn().setHeader("size", pageable.getPageSize());
                    }
                })
                .log("Enviando requisição GET para listar chatbots...")
                .to(nodeJsBaseUrl + "/api/chatbot")
                .log("Lista de chatbots obtida do Node.js. Total: ${body}")
                .unmarshal().json(List.class)
                .process(exchange -> {
                    @SuppressWarnings("unchecked")
                    List<Object> chatbots = exchange.getIn().getBody(List.class);

                    if (chatbots == null || chatbots.isEmpty()) {
                        exchange.getIn().setBody(null);
                    } else {
                        exchange.getIn().setBody(chatbots);
                    }
                });

        from("direct:nodejs-enviar-mensagem")
                .routeId("nodejs-enviar-mensagem")
                .log("Iniciando envio de mensagem via Node.js. Número: ${header.numero}")
                .process(exchange -> {
                    String numero = exchange.getIn().getHeader("numero", String.class);
                    @SuppressWarnings("unchecked")
                    Map<String, Object> mensagem = exchange.getIn().getBody(Map.class);

                    if (numero == null || numero.trim().isEmpty()) {
                        throw new IllegalArgumentException("Número do chatbot é obrigatório");
                    }

                    if (mensagem == null || mensagem.isEmpty()) {
                        throw new IllegalArgumentException("Mensagem não pode estar vazia");
                    }

                    exchange.getIn().setHeader("Content-Type", "application/json");
                    exchange.getIn().setHeader(Exchange.HTTP_METHOD, "POST");
                    exchange.getIn().setHeader("numeroDestino", numero);
                })
                .marshal().json()
                .log("Enviando mensagem para Node.js...")
                .toD(nodeJsBaseUrl + "/api/chatbot/${header.numeroDestino}/enviar")
                .log("Mensagem enviada com sucesso via Node.js. Resposta: ${body}");
    }
}

