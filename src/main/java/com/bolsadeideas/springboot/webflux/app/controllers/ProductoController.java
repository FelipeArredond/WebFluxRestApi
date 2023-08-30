package com.bolsadeideas.springboot.webflux.app.controllers;

import com.bolsadeideas.springboot.webflux.app.models.documents.Producto;
import com.bolsadeideas.springboot.webflux.app.models.services.ProductoService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/api/productos")
public class ProductoController {
    private final ProductoService productoService;

    public ProductoController(ProductoService productoService) {
        this.productoService = productoService;
    }

    @GetMapping
    public Mono<ResponseEntity<Flux<Producto>>> lista() {
        return Mono.just(
                ResponseEntity.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(this.productoService.findAll())
        );
    }

    @GetMapping("/{id}")
    public Mono<ResponseEntity<Producto>> obtenerUno(@PathVariable String id) {
        return this.productoService.findById(id)
                .map(p -> ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }
}
