package dev.aja.aja.directmessage.entity;

import java.util.ArrayList;
import java.util.List;

import dev.aja.aja.directmessage.dto.DirectMessageDTO;
import dev.aja.aja.user.entity.UserEntity;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * Clase para relacionar los usuarios participantes y los mensajes en una
 * conversación privada, DirectMessageEntity
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity
@Table(name = "directmessage")
public class DirectMessageEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToMany
    @JoinTable(name = "conversation_users", joinColumns = @JoinColumn(name = "conversation_id"), inverseJoinColumns = @JoinColumn(name = "user_id"))
    private List<UserEntity> participants;

    @Builder.Default
    @OneToMany(mappedBy = "directMessage", cascade = CascadeType.ALL)
    private List<MessageEntity> messages = new ArrayList<>();

    /**
     * Cuando hay un mensaje nuevo, se añade a la conversación
     * 
     * @param messageEntity información del mensaje nuevo a añadir del tipo
     *                      MessageEntity
     * @param userOne       Uno de los dos suers
     * @param userTwo       Segundo de los dos suers
     */
    public void addMessage(MessageEntity messageEntity) {
        this.messages.add(messageEntity);
    }

    public DirectMessageDTO toDTO() {

        return DirectMessageDTO.builder()
                .id(this.id)
                .participants(this.participants.stream().map(user -> user.toDTO()).toList())
                .messages(this.messages.stream().map(msg -> msg.toDTO()).toList())
                .build();
    }

}
