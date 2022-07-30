package com.cf.springbootbackendapirest.models.services;

import com.cf.springbootbackendapirest.models.entities.Cliente;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClienteService {

    List<Cliente> findAll();
    Page<Cliente> findAll(Pageable pageable);
    Cliente findById(Integer id);
    Cliente save(Cliente cliente);
    void delete(Integer id);
}
