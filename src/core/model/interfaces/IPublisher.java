package core.model.interfaces;

import java.util.List;

public interface IPublisher {
    String getNit();

    void setNit(String nit);

    String getNombre();

    void setNombre(String nombre);

    String getDireccion();

    void setDireccion(String direccion);

    IManager getGerente();

    void setGerente(IManager gerente);

    List<IStand> getStands();

    void setStands(List<IStand> stands);

    List<IBook> getLibros();

    void setLibros(List<IBook> libros);

    IPublisher copiar();
}
