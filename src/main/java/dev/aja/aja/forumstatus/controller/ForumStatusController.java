package dev.aja.aja.forumstatus.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.stereotype.Controller;

import dev.aja.aja.forumstatus.service.ForumStatusService;

/**
 * Controlador donde se especifican los MessageMapping (endpoints) para realizar
 * notificaciones por parte del usuario que escribe o deja de escribir un post
 */
@Controller
public class ForumStatusController {

    private final ForumStatusService forumstatusService;

    public ForumStatusController(ForumStatusService notificacionService) {
        this.forumstatusService = notificacionService;

    }

    @MessageMapping("/notify")
    public void getForumActivity(String info) {
        forumstatusService.addNotify(info);
    }

    @MessageMapping("/finish")
    public void delForumActivity(String info) {
        forumstatusService.delNotify(info);
    }

}