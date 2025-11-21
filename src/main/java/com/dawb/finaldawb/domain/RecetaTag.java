package com.dawb.finaldawb.domain;

import jakarta.persistence.*;
import java.util.Objects;

@Entity
@Table(name = "receta_tag")
public class RecetaTag {
    // Clave primaria compuesta, mapeada a la clase Embeddable
    @EmbeddedId
    private RecetaTagId id;

    // Mapeo de la FK a Receta
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("recetaId") // Mapea el campo 'recetaId' del Embeddable al ID de Receta
    @JoinColumn(name = "receta_id", nullable = false)
    private Receta receta;

    // Mapeo de la FK a Tag
    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("tagId") // Mapea el campo 'tagId' del Embeddable al ID de Tag
    @JoinColumn(name = "tag_id", nullable = false)
    private Tag tag;

    // Constructores, Getters y Setters

    public RecetaTag() {
        this.id = new RecetaTagId();
    }

    // Metodo de ayuda para crear la relación
    public RecetaTag(Receta receta, Tag tag) {
        this.receta = receta;
        this.tag = tag;
        this.id = new RecetaTagId(receta.getId(), tag.getId());
    }

    // Getters y Setters

    public RecetaTagId getId() { return id; }
    public void setId(RecetaTagId id) { this.id = id; }

    public Receta getReceta() { return receta; }
    // IMPORTANTE: Al establecer el objeto, también se debe actualizar el ID compuesto
    public void setReceta(Receta receta) {
        this.receta = receta;
        if (this.id == null) {
            this.id = new RecetaTagId();
        }
        this.id.setRecetaId(receta.getId());
    }

    public Tag getTag() { return tag; }
    public void setTag(Tag tag) {
        this.tag = tag;
        if (this.id == null) {
            this.id = new RecetaTagId();
        }
        this.id.setTagId(tag.getId());
    }

    // NOTA: Para las entidades de unión, equals y hashCode se basan en el ID incrustado
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof RecetaTag that)) return false;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}