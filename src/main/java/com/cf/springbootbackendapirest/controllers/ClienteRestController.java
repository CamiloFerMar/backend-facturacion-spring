package com.cf.springbootbackendapirest.controllers;

import com.cf.springbootbackendapirest.models.entities.Cliente;
import com.cf.springbootbackendapirest.models.services.ClienteService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.dao.DataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;


@CrossOrigin(origins = {"http://localhost:4200"})
@RestController
@RequestMapping("/clientes")
public class ClienteRestController {

    private final ClienteService clienteService;
    private final Logger log = LoggerFactory.getLogger(ClienteRestController.class);

    @Autowired
    public ClienteRestController(ClienteService clienteService) {
        this.clienteService = clienteService;
    }

    @GetMapping()
    public ResponseEntity<?> getClientes() {
        Map<String, Object> response = new HashMap<>();
        List<Cliente> clientes = clienteService.findAll();
        if (clientes.isEmpty()){
            response.put("message", "Clientes not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/page/{page}")
    public ResponseEntity<?> getClientes(@PathVariable Integer page) {
        Map<String, Object> response = new HashMap<>();
        Page<Cliente> clientes = clienteService.findAll(PageRequest.of(page, 4));
        if (clientes.isEmpty()){
            response.put("message", "Clientes not found");
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(clientes);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getCliente(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        Cliente cliente = clienteService.findById(id);
        if (cliente == null){
            String message = String.format("Cliente with id %d not found", id);
            response.put("message", message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return ResponseEntity.ok(cliente);
    }

    @PostMapping()
    public ResponseEntity<?> insertCliente(@Valid @RequestBody Cliente cliente, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        Cliente clienteInsert;
        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(error -> "El campo '" + error.getField() + "' " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try {
            clienteInsert = clienteService.save(cliente);
        }catch (DataAccessException e) {
            response.put("message", "Error al crear");
            response.put("error", e.getMostSpecificCause().getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(clienteInsert);
    }

    @PutMapping("/{id}")

    public ResponseEntity<?> updateCliente(@Valid @RequestBody Cliente cliente, @PathVariable Integer id, BindingResult result) {
        Map<String, Object> response = new HashMap<>();
        Cliente clienteUpdate;

        if (result.hasErrors()) {
            List<String> errors = result.getFieldErrors()
                    .stream()
                    .map(error -> "El campo '" + error.getField() + "' " + error.getDefaultMessage())
                    .collect(Collectors.toList());

            response.put("errors", errors);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
        }

        try{
            clienteUpdate = clienteService.findById(id);
        }catch (DataAccessException e){
            response.put("message", "Error al buscar");
            response.put("error", e.getMostSpecificCause().getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        if (clienteUpdate == null){
            String message = String.format("Cliente with id %d not found", id);
            response.put("error", message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        clienteUpdate.setNombre(cliente.getNombre());
        clienteUpdate.setApellido(cliente.getApellido());

        Cliente save;
        try{
            save = clienteService.save(clienteUpdate);
        }catch (DataAccessException e){
            response.put("message", "Error al guardar cambios");
            response.put("error", e.getMostSpecificCause().getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        return ResponseEntity.status(HttpStatus.OK).body(save);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteCliente(@PathVariable Integer id) {
        Map<String, Object> response = new HashMap<>();
        Cliente clienteDelete;

        try{
            clienteDelete = clienteService.findById(id);
        }catch (DataAccessException e){
            response.put("message", "Error al buscar");
            response.put("error", e.getMostSpecificCause().getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        if (clienteDelete == null){
            String message = String.format("Cliente with id %d not found", id);
            response.put("message", message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        try {
            String nombreFotoAnterior = clienteDelete.getFoto();
            if (nombreFotoAnterior != null && nombreFotoAnterior.length() > 0){
                Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
                File archivoFotoAnterior = rutaFotoAnterior.toFile();
                if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()){
                    archivoFotoAnterior.delete();
                }
            }
            clienteService.delete(id);
        }catch (DataAccessException e) {
            response.put("message", "Error al intentar eliminar");
            response.put("error", e.getMostSpecificCause().getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }
        response.put("message", String.format("Cliente with id %d deleted", id));
        return ResponseEntity.ok(response);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> upload(@RequestParam("file") MultipartFile archivo, @RequestParam("id") Integer id){
        Map<String, Object> response = new HashMap<>();
        Cliente clienteFound;
        try{
            clienteFound = clienteService.findById(id);
        }catch (DataAccessException e){
            response.put("message", "Error al buscar");
            response.put("error", e.getMostSpecificCause().getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
        }

        if (clienteFound == null){
            String message = String.format("Cliente with id %d not found", id);
            response.put("message", message);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }

        if(!archivo.isEmpty()){
            String nameFile = UUID.randomUUID() + "_" + archivo.getOriginalFilename().replace(" ", "");
            Path rutaArchivo = Paths.get("uploads").resolve(nameFile).toAbsolutePath();
            log.info(rutaArchivo.toString());

            try {
                Files.copy(archivo.getInputStream(), rutaArchivo);
            } catch (IOException e) {
                response.put("message", "Error al subir la imagen del cliente: " + nameFile);
                response.put("error", e.getCause().getMessage());
                return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
            }

            String nombreFotoAnterior = clienteFound.getFoto();

            if (nombreFotoAnterior != null && nombreFotoAnterior.length() > 0){
                Path rutaFotoAnterior = Paths.get("uploads").resolve(nombreFotoAnterior).toAbsolutePath();
                File archivoFotoAnterior = rutaFotoAnterior.toFile();
                if (archivoFotoAnterior.exists() && archivoFotoAnterior.canRead()){
                    archivoFotoAnterior.delete();
                }
            }

            clienteFound.setFoto(nameFile);
            clienteService.save(clienteFound);

            response.put("cliente", clienteFound);
            response.put("message", "Ha subido correctamente la imagen: " + nameFile);

        }

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/uploads/img/{nameFile:.+}")
    public ResponseEntity<Resource> showImg(@PathVariable String nameFile){
        Path rutaArchivo = Paths.get("uploads").resolve(nameFile).toAbsolutePath();
        log.info(rutaArchivo.toString());
        Resource recurso = null;

        try {
            recurso = new UrlResource(rutaArchivo.toUri());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        if (!recurso.exists() && !recurso.isReadable())
            throw new RuntimeException("Error no se pudo cargar la imagen: " + nameFile);

        HttpHeaders cabecera = new HttpHeaders();
        cabecera.add(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\""+ recurso.getFilename() + "\"");
        return new ResponseEntity<>(recurso, cabecera, HttpStatus.OK);
    }
}
