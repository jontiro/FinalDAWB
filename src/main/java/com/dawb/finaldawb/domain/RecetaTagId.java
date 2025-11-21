package com.dawb.finaldawb.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.io.Serializable;
import java.util.Objects;

@Embeddable // Indica que esta clase se puede incrustar como un ID
public class RecetaTagId implements Serializable {
    // Debe coincidir con el campo en RecetaTag.java y la columna FK en la BD
    @Column(name = "receta_id")
    private Long recetaId;

    @Column(name = "tag_id")
    private Long tagId;

    // Los constructores por defecto son requeridos por JPA
    public RecetaTagId() {}

    public RecetaTagId(Long recetaId, Long tagId) {
        this.recetaId = recetaId;
        this.tagId = tagId;
    }

    // Getters y Setters
    public Long getRecetaId() { return recetaId; }
    public void setRecetaId(Long recetaId) { this.recetaId = recetaId; }
    public Long getTagId() { return tagId; }
    public void setTagId(Long tagId) { this.tagId = tagId; }


    // CRÍTICO: Los métodos equals y hashCode deben usar ambos campos
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RecetaTagId that = (RecetaTagId) o;
        return Objects.equals(recetaId, that.recetaId) &&
                Objects.equals(tagId, that.tagId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(recetaId, tagId);
    }
}