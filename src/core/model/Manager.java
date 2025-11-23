package core.model;

/**
 * Representa a un gerente.
 */
public class Manager extends Person {

    private Publisher editorial;

    public Manager(long id, String nombres, String apellidos) {
        super(id, nombres, apellidos);
    }

    public Publisher getEditorial() {
        return editorial;
    }

    public void setEditorial(Publisher editorial) {
        this.editorial = editorial;
    }

    @Override
    public Manager copiar() {
        Manager copia = new Manager(this.id, this.nombres, this.apellidos);
        if (this.editorial != null) {
            copia.setEditorial(this.editorial.copiar());
        }
        return copia;
    }
}
