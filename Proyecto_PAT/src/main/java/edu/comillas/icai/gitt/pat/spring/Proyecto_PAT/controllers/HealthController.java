package edu.comillas.icai.gitt.pat.spring.Proyecto_PAT.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HealthController {

    @GetMapping("/pistaPadel/health")
    public String health() {
        return "ok";
    }
}
