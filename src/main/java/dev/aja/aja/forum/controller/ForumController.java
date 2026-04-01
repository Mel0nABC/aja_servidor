package dev.aja.aja.forum.controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

import dev.aja.aja.topic.dto.ForumEntityNewDTO;

@Controller
@RequestMapping("/api")
public class ForumController {

    @PostMapping("/forum")
    public ResponseEntity<Map<String, Object>> addForum(@RequestBody ForumEntityNewDTO forumEntityNewDTO) {

        System.out.println(SecurityContextHolder.getContext().getAuthentication());

        System.out.println("NEW FORUM: ");
        return ResponseEntity
                .ok(Map.of("success", true, "message", "Nuevo Forum añadido satisfactoriamente"));
    }

    @DeleteMapping("/forum/{id}")
    public ResponseEntity<Map<String, Object>> delForum(@PathVariable Long id) {
        System.out.println("DELETE FORUM SECTION: " + id);
        return ResponseEntity
                .ok(Map.of("success", true, "message", "Forum eliminado satisfactoriamente"));
    }

    @PutMapping("/forum")
    public ResponseEntity<Map<String, Object>> editForum() {
        System.out.println("EDITAMOS: ");
        return ResponseEntity
                .ok(Map.of("success", true, "message", "Forum editado satisfactoriamente"));
    }

    @GetMapping("/forum/{id}")
    public ResponseEntity<Map<String, Object>> getForum(@PathVariable Long id) {
        System.out.println("GET FORUM SECTION: " + id);
        return ResponseEntity
                .ok(Map.of("success", true, "message", "INFO TOTAL DE UN FORUM"));
    }

    @GetMapping("/forum")
    public ResponseEntity<Map<String, Object>> getAllForum() {
        System.out.println("GET ALL FORUMS");
        return ResponseEntity
                .ok(Map.of("success", true, "message", "INFO REDUCIDA (DTO) DE TODOS LOS FORUMS"));
    }

}