package br.com.BotAe.repository;

import br.com.BotAe.model.Chatbot;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ChatbotRepository extends JpaRepository<Chatbot, String> {

    @Query("""
        SELECT c FROM Chatbot c WHERE c.numero = :numero
    """)
    Optional<Chatbot> findByNumero(String numero);
}


