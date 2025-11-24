package core.model.interfaces;

public interface IPerson {
    long getId();

    void setId(long id);

    String getNombres();

    void setNombres(String nombres);

    String getApellidos();

    void setApellidos(String apellidos);

    String getNombreCompleto();

    IPerson copiar();
}
