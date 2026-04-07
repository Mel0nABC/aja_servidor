package dev.aja.aja.apidocs.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

/**
 * Controlador para poder proporcionar la api a los compañeros de Aja Team
 */
@Controller
public class ApidocsController {

    /**
     * Método que es llamado mediante un método tipo GET a la ruta /apidocs.
     * 
     * @return Nos reenvía al index.html de la apidocs, ubicado en
     *         resources/static/apidocs
     */
    @GetMapping("/apidocs")
    public String apidocs() {
        return "redirect:/apidocs/index.html";
    }
}
