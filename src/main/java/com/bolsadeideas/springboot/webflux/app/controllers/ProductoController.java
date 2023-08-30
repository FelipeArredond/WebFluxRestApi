package com.bolsadeideas.springboot.webflux.app.controllers;

import com.bolsadeideas.springboot.webflux.app.models.documents.Producto;
import com.bolsadeideas.springboot.webflux.app.models.services.ProductoService;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.Date;

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

    @PostMapping
    public Mono<ResponseEntity<Producto>> creat(@RequestBody Producto producto) {
        if (producto.getCreateAt() == null) {
            producto.setCreateAt(new Date());
        }
        return this.productoService.save(producto)
                .map(p -> ResponseEntity.created(URI.create("/api/productos/" + p.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.badRequest().build());
    }

    @PutMapping("/{id}")
    public Mono<ResponseEntity<Producto>> editar(@PathVariable String id, @RequestBody Producto producto) {
        return this.productoService.findById(id)
                .flatMap(p -> {
                    p.setNombre(producto.getNombre());
                    p.setCategoria(producto.getCategoria());
                    p.setPrecio(producto.getPrecio());
                    return this.productoService.save(p);
                }).map(p -> ResponseEntity.created(URI.create("/api/productos/" + p.getId()))
                        .contentType(MediaType.APPLICATION_JSON)
                        .body(p))
                .defaultIfEmpty(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<Void>> eliminar(@PathVariable String id) {
        return this.productoService.findById(id)
                .flatMap(p -> {
                    return this.productoService.delete(p)
                            .then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
                }).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
    }
}

