package com.dawb.finaldawb.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.Objects;

@Entity
@Table(name = "objeto", uniqueConstraints = {
        // Mapea la clave única en la descripción
        @UniqueConstraint(name = "uk_objeto_descripcion", columnNames = "descripcion")
})
public class Objeto {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Mapea a descripcion (VARCHAR(100) NOT NULL)
    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String descripcion;

    // Auditoría
    @Column(name = "creado_en", nullable = false, updatable = false)
    private Instant creadoEn;

    @Column(name = "actualizado_en", nullable = false)
    private Instant actualizadoEn;

    // Métodos de Ciclo de Vida para Auditoría
    @PrePersist
    protected void onCreate() {
        if (creadoEn == null) creadoEn = Instant.now();
        if (actualizadoEn == null) actualizadoEn = creadoEn;
    }

    @PreUpdate
    protected void onUpdate() {
        actualizadoEn = Instant.now();
    }

    // Getters y Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Instant getCreadoEn() {
        return creadoEn;
    }

    public void setCreadoEn(Instant creadoEn) {
        this.creadoEn = creadoEn;
    }

    public Instant getActualizadoEn() {
        return actualizadoEn;
    }

    public void setActualizadoEn(Instant actualizadoEn) {
        this.actualizadoEn = actualizadoEn;
    }

    // Contrato JPA: equals y hashCode basados en el ID
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Objeto objeto)) return false;
        return Objects.equals(id, objeto.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }
}