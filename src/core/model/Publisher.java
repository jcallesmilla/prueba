package core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa una editorial.
 */
public class Publisher {

    private String nit;
    private String nombre;
    private String direccion;
    private Manager gerente;
    private List<Stand> stands;
    private List<Book> libros;

    public Publisher(String nit, String nombre, String direccion, Manager gerente) {
        this.nit = nit;
        this.nombre = nombre;
        this.direccion = direccion;
        this.gerente = gerente;
        this.stands = new ArrayList<>();
        this.libros = new ArrayList<>();
    }

    public String getNit() {
        return nit;
    }

    public void setNit(String nit) {
        this.nit = nit;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public Manager getGerente() {
        return gerente;
    }

    public void setGerente(Manager gerente) {
        this.gerente = gerente;
    }

    public List<Stand> getStands() {
        return stands;
    }

    public void setStands(List<Stand> stands) {
        this.stands = stands;
    }

    public List<Book> getLibros() {
        return libros;
    }

    public void setLibros(List<Book> libros) {
        this.libros = libros;
    }

    public Publisher copiar() {
        Publisher copia = new Publisher(this.nit, this.nombre, this.direccion, null);
        
        // Copiar gerente
        if (this.gerente != null) {
            Manager gerenteCopia = new Manager(
                this.gerente.getId(), 
                this.gerente.getNombres(), 
                this.gerente.getApellidos()
            );
            copia.setGerente(gerenteCopia);
        }
        
        // Copiar stands (solo datos básicos, SIN editoriales para evitar ciclo)
        List<Stand> copiasStands = new ArrayList<>();
        if (this.stands != null) {
            for (Stand stand : this.stands) {
                Stand copiaStand = new Stand(stand.getId(), stand.getPrecio());
                // NO copiar las editoriales del stand (evita el ciclo)
                copiasStands.add(copiaStand);
            }
        }
        copia.setStands(copiasStands);
        
        // Copiar libros (ya está bien implementado)
        List<Book> copiasLibros = new ArrayList<>();
        if (this.libros != null) {
            for (Book libro : this.libros) {
                copiasLibros.add(libro.copiar());
            }
        }
        copia.setLibros(copiasLibros);
        
        return copia;
    }
}
