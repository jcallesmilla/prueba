package core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa un stand en la feria.
 */
public class Stand {

    private long id;
    private double precio;
    private List<Publisher> editoriales;

    public Stand(long id, double precio) {
        this.id = id;
        this.precio = precio;
        this.editoriales = new ArrayList<>();
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public double getPrecio() {
        return precio;
    }

    public void setPrecio(double precio) {
        this.precio = precio;
    }

    public List<Publisher> getEditoriales() {
        return editoriales;
    }

    public void setEditoriales(List<Publisher> editoriales) {
        this.editoriales = editoriales;
    }

    public Stand copiar() {
        Stand copia = new Stand(this.id, this.precio);
        List<Publisher> copias = new ArrayList<>();
        for (Publisher editorial : this.editoriales) {
            copias.add(editorial.copiar());
        }
        copia.setEditoriales(copias);
        return copia;
    }
}
