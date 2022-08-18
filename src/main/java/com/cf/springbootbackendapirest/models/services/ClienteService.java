package com.cf.springbootbackendapirest.models.services;

import com.cf.springbootbackendapirest.models.entities.Cliente;
import com.cf.springbootbackendapirest.models.entities.Region;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ClienteService {

    List<Cliente> findAll();
    Page<Cliente> findAll(Pageable pageable);
    List<Region> findAllRegiones();
    Cliente findById(Integer id);
    Cliente save(Cliente cliente);
    void delete(Integer id);
}
