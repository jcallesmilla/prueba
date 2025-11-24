package core.model.interfaces;

public interface IManager extends IPerson {
    IPublisher getEditorial();

    void setEditorial(IPublisher editorial);

    IManager copiar();
}
