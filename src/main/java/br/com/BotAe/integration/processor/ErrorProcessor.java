package br.com.BotAe.integration.processor;

import br.com.BotAe.exception.IntegracaoException;
import org.apache.camel.Exchange;
import org.apache.camel.Processor;
import org.springframework.stereotype.Component;

import java.io.Serializable;

@Component
public class ErrorProcessor implements Processor, Serializable {

    @Override
    public void process(Exchange exchange) {
        Exception error = exchange.getProperty("error", Exception.class);
         throw new IntegracaoException(error.getMessage());
    }
}
