package core.model;

import core.model.interfaces.IPublisher;
import core.model.interfaces.IManager;
import core.model.interfaces.IStand;
import core.model.interfaces.IBook;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa una editorial.
 */
public class Publisher implements IPublisher {

    private String nit;
    private String nombre;
    private String direccion;
    private IManager gerente;
    private List<IStand> stands;
    private List<IBook> libros;

    public Publisher(String nit, String nombre, String direccion, IManager gerente) {
        this.nit = nit;
        this.nombre = nombre;
        this.direccion = direccion;
        this.gerente = gerente;
        this.stands = new ArrayList<>();
        this.libros = new ArrayList<>();
    }

    @Override
    public String getNit() {
        return nit;
    }

    @Override
    public void setNit(String nit) {
        this.nit = nit;
    }

    @Override
    public String getNombre() {
        return nombre;
    }

    @Override
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    @Override
    public String getDireccion() {
        return direccion;
    }

    @Override
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    @Override
    public IManager getGerente() {
        return gerente;
    }

    @Override
    public void setGerente(IManager gerente) {
        this.gerente = gerente;
    }

    @Override
    public List<IStand> getStands() {
        return stands;
    }

    @Override
    public void setStands(List<IStand> stands) {
        this.stands = stands;
    }

    @Override
    public List<IBook> getLibros() {
        return libros;
    }

    @Override
    public void setLibros(List<IBook> libros) {
        this.libros = libros;
    }

    @Override
    public IPublisher copiar() {
        Publisher copia = new Publisher(this.nit, this.nombre, this.direccion, null);

        // Copiar gerente
        if (this.gerente != null) {
            IManager gerenteCopia = new Manager(
                    this.gerente.getId(),
                    this.gerente.getNombres(),
                    this.gerente.getApellidos());
            copia.setGerente(gerenteCopia);
        }

        // Copiar stands (solo datos básicos, SIN editoriales para evitar ciclo)
        List<IStand> copiasStands = new ArrayList<>();
        if (this.stands != null) {
            for (IStand stand : this.stands) {
                IStand copiaStand = new Stand(stand.getId(), stand.getPrecio());
                // NO copiar las editoriales del stand (evita el ciclo)
                copiasStands.add(copiaStand);
            }
        }
        copia.setStands(copiasStands);

        // Copiar libros (ya está bien implementado)
        List<IBook> copiasLibros = new ArrayList<>();
        if (this.libros != null) {
            for (IBook libro : this.libros) {
                copiasLibros.add(libro.copiar());
            }
        }
        copia.setLibros(copiasLibros);

        return copia;
    }
}
