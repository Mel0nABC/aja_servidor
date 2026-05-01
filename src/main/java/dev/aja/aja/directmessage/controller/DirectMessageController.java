package dev.aja.aja.directmessage.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.aja.aja.directmessage.dto.DirectMessageNewDTO;
import dev.aja.aja.directmessage.service.DirectMessageService;

/**
 * Clase que nos va a gestionar todos los endpoints refentes a DirectMessage,
 * obtener, añadir y eliminar
 */
@Controller
@RequestMapping("/api")
public class DirectMessageController {

    private final DirectMessageService directMessageService;

    /**
     * 
     * Constructor para implementar inyección de dependencias necesarias
     * 
     * @param directMessageService inyección para servicio de directmessage,
     *                             obtenemos acceso a la lógica referente a usuarios
     */
    public DirectMessageController(DirectMessageService directMessageService) {
        this.directMessageService = directMessageService;
    }

    /**
     * Para añadir nuevo mensaje a una conversación o se creará si esta no existeF
     * 
     * @param newMessageDTO datos necesario para crear nuevo mensaje o conversación
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un mensaje de texto, si hubiera algún fallo llegarían los
     *         diccionarios de las excepciones
     */
    @PutMapping("/dm")
    public ResponseEntity<Map<String, Object>> addDirectMessage(@RequestBody DirectMessageNewDTO newMessageDTO) {

        directMessageService.addDirectMessage(newMessageDTO);

        return ResponseEntity.ok(Map.of("success", true, "message", "Mensaje enviado satisfactoriamente"));

    }

    /**
     * Obtener la conversación con el usuario de otherUserId
     * 
     * @param otherUserId id del usuario con el que se tiene la conversación
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es DirectMessageEntity, si hubiera algún fallo llegarían los
     *         diccionarios de las excepciones
     */
    @GetMapping("/dm/{otherUserId}")
    public ResponseEntity<Map<String, Object>> getDirectMessage(@PathVariable Long otherUserId) {

        return ResponseEntity
                .ok(Map.of("success", true, "message", directMessageService.getDirectMessage(otherUserId)));

    }

    /**
     * Para eliminar la conversación
     * 
     * @param otherUserId id del usuario con el que se tiene la conversación
     * @return retornamos un diccionario, success indica cuál ha sido el resultado y
     *         message el contenido. En este caso el contenido de respuesta válida
     *         es un mensaje de texto, si hubiera algún fallo llegarían los
     *         diccionarios de las excepciones
     */
    @DeleteMapping("/dm/{otherUserId}")
    public ResponseEntity<Map<String, Object>> delDirectMessage(@PathVariable Long otherUserId) {

        directMessageService.delDirectMessage(otherUserId);

        return ResponseEntity.ok(Map.of("success", true, "message", "Conversación eliminada satisfactoriamente"));

    }

}
