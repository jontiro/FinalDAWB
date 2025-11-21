package com.dawb.finaldawb.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.util.Objects;

@Entity
@Table(name = "receta_paso", uniqueConstraints = {
        @UniqueConstraint(name = "uk_receta_paso_orden", columnNames = {"receta_id", "orden"})
})
public class RecetaPaso {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // RELACIÓN Muchos-a-Uno (M:1): Muchos pasos pertenecen a UNA Receta
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    // Orden del paso (Mapea a smallint unsigned)
    @NotNull
    @PositiveOrZero // Asegura que no sea negativo, reflejando el 'unsigned' de la BD
    @Column(nullable = false)
    private Short orden;

    // Descripción del paso (Mapea a TEXT)
    @NotBlank
    @Lob // Indica que es un objeto grande (TEXT/CLOB)
    @Column(nullable = false)
    private String descripcion;

    // Getters and Setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Receta getReceta() {
        return receta;
    }

    public void setReceta(Receta receta) {
        this.receta = receta;
    }

    public Short getOrden() {
        return orden;
    }

    public void setOrden(Short orden) {
        this.orden = orden;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecetaPaso that)) return false;
        // La igualdad se basa en el ID (después de la persistencia)
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}