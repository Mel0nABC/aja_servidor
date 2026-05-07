package dev.aja.aja.forumstatus.service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import dev.aja.aja.forumstatus.dto.NotifyStatusDTO;

/**
 * Service con toda la lógica empresarial
 */
@EnableScheduling
@Service
public class ForumStatusService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper mapper = new ObjectMapper();
    private Map<NotifyStatusDTO, Instant> notificationList = new HashMap<>();
    private final int CHECK_TIME = 15000;
    private final int CHECK_TIME_SECONDS = CHECK_TIME / 1000;

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

        NotifyStatusDTO notify = convertStringToObject(info);

        notificationList.put(notify, Instant.now());

        System.out.println("####################### ADD #######################");
        System.out.println("####################### " + notificationList.size() + " #######################");
        System.out.println(notify);
        System.out.println("###################################################");

        notifyToBroker();
    }

    /**
     * Notificar que un usuario ya finalizo de escribir el post
     * 
     * @param info string de un diccionario del tipo NotifyStatusDTO
     */
    public void delNotify(String info) {

        NotifyStatusDTO notify = convertStringToObject(info);

        notificationList.remove(notify);

        System.out.println("####################### DEL #######################");
        System.out.println("####################### " + notificationList.size() + " #######################");
        System.out.println(notify);
        System.out.println("###################################################");

        notifyToBroker();
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

            List<NotifyStatusDTO> filteredList = new ArrayList<>(notificationList.keySet());

            String json = mapper.writeValueAsString(filteredList);
            messagingTemplate.convertAndSend("/status", json);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

    /**
     * Scheduler que se ejecuta cada los segundos indicados en CHECK_TIME
     */
    @Scheduled(fixedRate = CHECK_TIME)
    public void launchScheduler() {
        this.checkActivityNotificationList();
    }

    /**
     * Comprobamos si el valor de notificationList es mayor a CHECK_TIME_SECONDS, si
     * lo es, see limina esa entrada del map
     * 
     * Sirve para ir limpiando la lista de notificationList
     */
    public void checkActivityNotificationList() {
        notificationList.entrySet()
                .removeIf(n -> (Instant.now().getEpochSecond() - n.getValue().getEpochSecond()) > CHECK_TIME_SECONDS);

        System.out.println("CHECK LIST OF ACTIVITY");
        notificationList.entrySet().forEach(n -> {
            System.out.println(n.getKey() + " - " + n.getValue());
        });

    }

}
