package com.cf.springbootbackendapirest.models.respositories;

import com.cf.springbootbackendapirest.models.entities.Cliente;
import com.cf.springbootbackendapirest.models.entities.Region;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface ClienteDAO extends JpaRepository<Cliente, Integer> {
    @Query("from Region")
    List<Region> findAllRegiones();
}
