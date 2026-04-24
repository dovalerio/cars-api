package br.com.dovalerio.cars_api.config;

import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.Resource;
import org.springframework.http.CacheControl;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiDocsController {

    @GetMapping(value = "/api-docs", produces = "application/yaml")
    public ResponseEntity<Resource> apiDocs() {
        Resource resource = new ClassPathResource("static/api-docs.yaml");

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType("application/yaml"))
                .cacheControl(CacheControl.noCache())
                .body(resource);
    }
}