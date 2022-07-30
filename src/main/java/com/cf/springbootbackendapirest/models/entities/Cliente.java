package com.cf.springbootbackendapirest.models.entities;

import javax.persistence.*;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table(name = "clientes")
public class Cliente implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    @NotEmpty
    @Size(min = 4)
    @Column(nullable = false)
    private String nombre;
    @NotEmpty
    private String apellido;
    @NotEmpty
    @Email()
    @Column(unique = true)
    private String email;
    @NotNull
    @Column(name = "create_at")
    @Temporal(TemporalType.DATE)
    private Date createAt;

    public String getFoto() {
        return foto;
    }

    public void setFoto(String foto) {
        this.foto = foto;
    }

    private String foto;


    public Cliente(Integer id, String nombre, String apellido, String email, String foto) {
        this.id = id;
        this.nombre = nombre;
        this.apellido = apellido;
        this.email = email;
        this.foto = foto;
    }

    public Cliente() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Date getCreateAt() {
        return createAt;
    }

    public void setCreateAt(Date createAt) {
        this.createAt = createAt;
    }

    /*@PrePersist
    void prePersist() {
        this.createAt = LocalDateTime.now();
    }*/
    /*@PreUpdate
    void preUpdate() {
        this.createAt = LocalDateTime.now();
    }*/
}
