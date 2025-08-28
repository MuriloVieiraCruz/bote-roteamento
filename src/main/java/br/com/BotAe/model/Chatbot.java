package br.com.BotAe.model;

import br.com.BotAe.model.enums.Status;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "chatbots")
@EntityListeners(AuditingEntityListener.class)
public class Chatbot {

    @Id
    @Column(name = "numero")
    @NotBlank(message = "O número vinculado é obrigatório")
    private String numero;

    @Column(name = "webhook")
    @NotBlank(message = "O webhook é obrigatório")
    private String webhook;

    @Column(name = "qr_code", columnDefinition = "TEXT")
    @NotBlank(message = "O código QR é obrigatório")
    private String qrCode64;

    @Column(name = "conectado")
    @Enumerated(EnumType.STRING)
    private Status isConectado;

    @Column(name = "pronto")
    @Enumerated(EnumType.STRING)
    private Status isPronto;

    @Column(name = "recebe_mensagem")
    @Enumerated(EnumType.STRING)
    private Status isRecebeMsgs;

    @Column(name = "criado_em", updatable = false)
    @CreatedDate
    private LocalDateTime criadoEm;

    @Column(name = "atualizado_em")
    @LastModifiedDate
    private LocalDateTime atualizadoEm;
}
