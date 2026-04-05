package dev.aja.aja.demo;

import java.time.LocalDate;
import java.util.List;

import dev.aja.aja.forum.entity.ForumEntity;
import dev.aja.aja.forum.repository.ForumRepository;

/**
 * Clase para crear todos los ejemplos de la entity Forum
 */
public class DemoForums {

        private final List<ForumEntity> forumList;

        /**
         * Constructor, al cual le pasamos el forumRepository para guardar las
         * instancias de ForumEntity
         * 
         * @param forumRepository para guardar las entidades Forum creadas
         */
        public DemoForums(ForumRepository forumRepository) {
                forumList = createForumsTitles();
                forumRepository.saveAll(forumList);
        }

        /**
         * Creamos todos los ejemplos
         */
        public List<ForumEntity> createForumsTitles() {
                LocalDate dateNow = LocalDate.now();

                // Ejemplos creados con chatGPT

                ForumEntity hardware = ForumEntity.builder()
                                .title("Hardware")
                                .creationDate(dateNow)
                                .lastModification(dateNow)
                                .build();

                ForumEntity software = ForumEntity.builder()
                                .title("Software")
                                .creationDate(dateNow)
                                .lastModification(dateNow)
                                .build();

                ForumEntity programming = ForumEntity.builder()
                                .title("Programación")
                                .creationDate(dateNow)
                                .lastModification(dateNow)
                                .build();

                ForumEntity cybersecurity = ForumEntity.builder()
                                .title("Cyberseguridad")
                                .creationDate(dateNow)
                                .lastModification(dateNow)
                                .build();

                ForumEntity artificialIntelligence = ForumEntity.builder()
                                .title("Inteligencia Artificial")
                                .creationDate(dateNow)
                                .lastModification(dateNow)
                                .build();

                ForumEntity networking = ForumEntity.builder()
                                .title("Redes")
                                .creationDate(dateNow)
                                .lastModification(dateNow)
                                .build();

                ForumEntity linux = ForumEntity.builder()
                                .title("Linux & Servidores")
                                .creationDate(dateNow)
                                .lastModification(dateNow)
                                .build();

                ForumEntity mobile = ForumEntity.builder()
                                .title("Desarrollo móvil")
                                .creationDate(dateNow)
                                .lastModification(dateNow)
                                .build();

                ForumEntity gaming = ForumEntity.builder()
                                .title("Juegos y Desarrollo de Juegos")
                                .creationDate(dateNow)
                                .lastModification(dateNow)
                                .build();

                return List.of(hardware,
                                software,
                                programming,
                                cybersecurity,
                                artificialIntelligence,
                                networking,
                                linux,
                                mobile,
                                gaming);
        }

        /**
         * Lista de forums para poder realizar acciones
         * 
         * @return Lista de forums
         */
        public List<ForumEntity> getForumList() {
                return forumList;
        }
}
