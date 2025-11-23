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
        // NO copiar las editoriales para evitar ciclo infinito
        // Solo copiar información básica de las editoriales
        List<Publisher> copiasEditoriales = new ArrayList<>();
        if (this.editoriales != null) {
            for (Publisher editorial : this.editoriales) {
                // Crear una copia superficial solo con datos básicos
                Publisher copiaEditorial = new Publisher(
                    editorial.getNit(), 
                    editorial.getNombre(), 
                    editorial.getDireccion(), 
                    null // No copiar gerente para evitar complejidad
                );
                // NO copiar los stands de la editorial (evita el ciclo)
                copiasEditoriales.add(copiaEditorial);
            }
        }
        copia.setEditoriales(copiasEditoriales);
        return copia;
    }
}