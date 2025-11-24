package core.model;

import core.model.interfaces.IPerson;

/**
 * Clase base para personas dentro de la feria.
 * Solo guarda datos sencillos y ofrece getters, setters y un método para
 * copiar.
 */
public abstract class Person implements IPerson {

    protected long id;
    protected String nombres;
    protected String apellidos;

    public Person(long id, String nombres, String apellidos) {
        this.id = id;
        this.nombres = nombres;
        this.apellidos = apellidos;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }

    public String getApellidos() {
        return apellidos;
    }

    public void setApellidos(String apellidos) {
        this.apellidos = apellidos;
    }

    public String getNombreCompleto() {
        return nombres + " " + apellidos;
    }

    /**
     * Crea una copia sencilla de la persona.
     * 
     * @return copia sin compartir referencias.
     */
    public abstract IPerson copiar();
}
