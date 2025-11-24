package core.model;

/**
 * Representa a un gerente.
 */
import core.model.interfaces.IManager;
import core.model.interfaces.IPublisher;

public class Manager extends Person implements IManager {

    private IPublisher editorial;

    public Manager(long id, String nombres, String apellidos) {
        super(id, nombres, apellidos);
    }

    @Override
    public IPublisher getEditorial() {
        return editorial;
    }

    @Override
    public void setEditorial(IPublisher editorial) {
        this.editorial = editorial;
    }

    protected IManager crearCopiaSimple() {
        return new Manager(this.id, this.nombres, this.apellidos);
    }

    @Override
    public IManager copiar() {
        Manager copia = new Manager(this.id, this.nombres, this.apellidos);
        if (this.editorial != null) {
            copia.setEditorial(this.editorial.copiar());
        }
        return copia;
    }
}
