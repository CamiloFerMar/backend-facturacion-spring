package com.cf.springbootbackendapirest.models.respositories;

import com.cf.springbootbackendapirest.models.entities.Cliente;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ClienteDAO extends JpaRepository<Cliente, Integer> {
}
