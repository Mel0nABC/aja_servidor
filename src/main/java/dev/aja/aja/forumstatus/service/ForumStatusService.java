package dev.aja.aja.forumstatus.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.aja.aja.forumstatus.dto.NotifyStatusDTO;

/**
 * Service con toda la lógica empresarial
 */
@Service
public class ForumStatusService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    private List<NotifyStatusDTO> notificationList = new ArrayList<>();

    /**
     * Constructor con inyecciónd e dependencias
     * 
     * @param messagingTemplate instancia de objeto SimpMessagindTemplate
     */
    public ForumStatusService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    /**
     * Notificar que un usuario comenzó a escribir el post
     * 
     * @param info string de un diccionario del tipo NotifyStatusDTO
     */
    public void addNotify(String info) {

        notificationList.add(convertStringToObject(info));

        notifyToBroker();
    }

    /**
     * Notificar que un usuario ya finalizo de escribir el post
     * 
     * @param info string de un diccionario del tipo NotifyStatusDTO
     */
    public void delNotify(String info) {

        notificationList.remove(convertStringToObject(info));

        notifyToBroker();
    }

    public List<NotifyStatusDTO> getNotificationList() {
        return notificationList;
    }

    /**
     * Serializamos de string (diccionario) a NotifyStatusDTO
     * 
     * @param info string con estructura diccionario o json
     * 
     * @return Si ocurre algún problema al serializar, se devuelve null
     */
    public NotifyStatusDTO convertStringToObject(String info) {
        try {
            return mapper.readValue(info, NotifyStatusDTO.class);
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

        return null;
    }

    /**
     * Mediante toda la lista de notificaciones, la enviamos al broker para que
     * llegue dicha lista a todos los usuarios que están suscritos a /status
     */
    public void notifyToBroker() {
        try {

            String json = mapper.writeValueAsString(notificationList);
            messagingTemplate.convertAndSend("/status", json);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
