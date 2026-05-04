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

@Service
public class ForumStatusService {

    private final SimpMessagingTemplate messagingTemplate;
    private final ObjectMapper mapper = new ObjectMapper();

    public ForumStatusService(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    private List<NotifyStatusDTO> notificationList = new ArrayList<>();

    public void addNotify(String info) {

        notificationList.add(convertStringToObject(info));

        notifyToBroker();

        System.out.println("########################## AÑADIMOS NOTIFICACIÓN: " + notificationList.size()
                + " ##########################");

    }

    public void delNotify(String info) {

        notificationList.remove(convertStringToObject(info));

        notifyToBroker();

        System.out.println("########################## ELIMINAMOS NOTIFICACIÓN: " + notificationList.size()
                + " ##########################");

    }

    public List<NotifyStatusDTO> getNotificationList() {
        return notificationList;
    }

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

    public void notifyToBroker() {
        try {

            String json = mapper.writeValueAsString(notificationList);
            messagingTemplate.convertAndSend("/status", json);

        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }
    }

}
