package dev.aja.aja.demo;

import java.time.LocalDate;
import java.util.List;

import dev.aja.aja.forum.entity.ForumEntity;
import dev.aja.aja.forum.repository.ForumRepository;
import dev.aja.aja.topic.entity.TopicEntity;
import dev.aja.aja.user.entity.UserEntity;

public class DemoTopics {

    public DemoTopics(List<ForumEntity> forumList, DemoUsers demoUsers, ForumRepository forumRepository) {

        int count = 0;

        for (ForumEntity forum : forumList) {

            UserEntity owner = demoUsers.getAdmin();

            if (count < (forumList.size() / 2))
                owner = demoUsers.getUser();

            switch (forum.getTitle()) {

                case "Hardware":
                    forum.addTopic(TopicEntity.builder()
                            .title("Problemas de temperatura en CPU")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("¿Qué GPU comprar en 2025?")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Ampliar RAM en portátil")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Fuentes de alimentación recomendadas")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());
                    break;

                case "Software":
                    forum.addTopic(TopicEntity.builder()
                            .title("Mejores editores de texto")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Errores comunes en Windows")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Alternativas a Photoshop")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Software imprescindible 2025")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());
                    break;

                case "Programación":
                    forum.addTopic(TopicEntity.builder()
                            .title("Buenas prácticas en Java")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Diferencias entre List y Set")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Patrones de diseño más usados")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Streams vs Loops")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());
                    break;

                case "Cyberseguridad":
                    forum.addTopic(TopicEntity.builder()
                            .title("Cómo funciona un ataque phishing")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Buenas prácticas de contraseñas")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Introducción al hacking ético")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Autenticación de dos factores")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());
                    break;

                case "Inteligencia Artificial":
                    forum.addTopic(TopicEntity.builder()
                            .title("Introducción a Machine Learning")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Redes neuronales básicas")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("IA generativa en 2025")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Modelos de lenguaje")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());
                    break;

                case "Redes":
                    forum.addTopic(TopicEntity.builder()
                            .title("Qué es una VLAN")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Configurar router en casa")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Diferencias entre TCP y UDP")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("DNS explicado")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());
                    break;

                case "Linux & Servidores":
                    forum.addTopic(TopicEntity.builder()
                            .title("Comandos básicos de Linux")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Montar servidor Ubuntu")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Permisos en Linux")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Systemd servicios")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());
                    break;

                case "Desarrollo móvil":
                    forum.addTopic(TopicEntity.builder()
                            .title("Empezar con Android")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Flutter vs React Native")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Publicar app en Play Store")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Arquitectura MVVM móvil")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());
                    break;

                case "Juegos y Desarrollo de Juegos":
                    forum.addTopic(TopicEntity.builder()
                            .title("Introducción a Unity")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Motores gráficos más usados")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Cómo empezar en game dev")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());

                    forum.addTopic(TopicEntity.builder()
                            .title("Física en videojuegos")
                            .creationDate(LocalDate.now())
                            .lastModification(LocalDate.now())
                            .userOwner(owner)
                            .build());
                    break;
            }

            System.out.println(forum.getTitle());
            forumRepository.save(forum);
            count++;
        }
    }

}
