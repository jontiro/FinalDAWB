package com.dawb.finaldawb.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import java.time.Instant;
import java.util.Objects;
import java.util.List;

@Entity
@Table(name = "receta")


public class Receta {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank
    @Size(max = 100)
    @Column(nullable = false, length = 100)
    private String titulo;

    @NotBlank
    @Lob
    @Column(nullable = false)
    private String descripcion;

    @Column(name = "tiempo_preparacion", nullable = false)
    private Short tiempoPreparacion; // en minutos

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "creador_id", nullable = false)
    private Usuario creador;

    @NotNull
    @Column(name = "fecha_creacion", nullable = false, updatable = false)
    private Instant fechaCreacion;

    @NotNull
    @Column(name = "fecha_actualizacion", nullable = false)
    private Instant fechaActualizacion;

    @Column(name = "privacidad", nullable = false)
    private boolean privacidad = false; // 0: pública, 1: privada

    @PrePersist
    protected void onCreate() {
        if (fechaCreacion == null) fechaCreacion = Instant.now();
        if (fechaActualizacion == null) fechaActualizacion = Instant.now();
    }

    @PreUpdate
    protected void onUpdate() {
        fechaActualizacion = Instant.now();
    }

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    @OrderBy("orden ASC")
    private List<RecetaPaso> pasos;

    @OneToMany(mappedBy = "receta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<RecetaTag> recetaTags;


    // Getters and Setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Short getTiempoPreparacion() {
        return tiempoPreparacion;
    }

    public void setTiempoPreparacion(Short tiempoPreparacion) {
        this.tiempoPreparacion = tiempoPreparacion;
    }

    public Usuario getCreador() {
        return creador;
    }

    public void setCreador(Usuario creador) {
        this.creador = creador;
    }

    public Instant getFechaCreacion() {
        return fechaCreacion;
    }

    public void setFechaCreacion(Instant fechaCreacion) {
        this.fechaCreacion = fechaCreacion;
    }

    public Instant getFechaActualizacion() {
        return fechaActualizacion;
    }

    public void setFechaActualizacion(Instant fechaActualizacion) {
        this.fechaActualizacion = fechaActualizacion;
    }

    public boolean getPrivacidad() {
        return privacidad;
    }

    public void setPrivacidad(boolean privacidad) {
        this.privacidad = privacidad;
    }

    public boolean isPrivacidad() {
        return privacidad;
    }

    public List<RecetaPaso> getPasos() {
        return pasos;
    }

    public void setPasos(List<RecetaPaso> pasos) {
        this.pasos = pasos;
    }

    public List<RecetaTag> getRecetaTags() {
        return recetaTags;
    }

    public void setRecetaTags(List<RecetaTag> recetaTags) {
        this.recetaTags = recetaTags;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Receta receta)) return false;
        return Objects.equals(id, receta.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

}
