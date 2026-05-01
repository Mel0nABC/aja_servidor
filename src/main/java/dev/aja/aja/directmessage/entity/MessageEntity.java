package dev.aja.aja.directmessage.entity;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import dev.aja.aja.directmessage.dto.MessageEntityDTO;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase para organizar un nuevo mensaje de una conversación
 */
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@Entity
public class MessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private LocalDateTime dateTime = LocalDateTime.now();

    @Column(nullable = false)
    private Long fromId;

    @Column(nullable = false)
    private String fromName;

    @Column(nullable = false)
    private String message;

    @ManyToOne
    @JoinColumn(name = "direct_message_id", nullable = false)
    private DirectMessageEntity directMessage;

    /**
     * Se sobre escribe toString() para que el front pueda obtener una línea de la
     * conversación ya formateada
     */
    public String toString() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:mm:ss");
        return this.dateTime.format(formatter) + ", " + fromName + ": " + message;
    }

    public MessageEntityDTO toDTO() {
        return MessageEntityDTO.builder()
                .id(this.id)
                .dateTime(this.dateTime)
                .fromId(this.fromId)
                .fromName(this.fromName)
                .message(this.message)
                .build();
    }

}
