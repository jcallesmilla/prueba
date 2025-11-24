package core.model;

import core.model.interfaces.IAuthor;
import core.model.interfaces.IBook;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un autor.
 */
public class Author extends Person implements IAuthor {

    private List<IBook> libros;

    public Author(long id, String nombres, String apellidos) {
        super(id, nombres, apellidos);
        this.libros = new ArrayList<>();
    }

    @Override
    public List<IBook> getLibros() {
        return libros;
    }

    @Override
    public void setLibros(List<IBook> libros) {
        this.libros = libros;
    }

    protected IAuthor crearCopiaSimple() {
        return new Author(this.id, this.nombres, this.apellidos);
    }

    @Override
    public IAuthor copiar() {
        Author copia = new Author(this.id, this.nombres, this.apellidos);
        List<IBook> copiasLibros = new ArrayList<>();
        for (IBook libro : this.libros) {
            copiasLibros.add(libro.copiar());
        }
        copia.setLibros(copiasLibros);
        return copia;
    }
}
