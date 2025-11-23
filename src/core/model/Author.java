package core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un autor.
 */
public class Author extends Person {

    private List<Book> libros;

    public Author(long id, String nombres, String apellidos) {
        super(id, nombres, apellidos);
        this.libros = new ArrayList<>();
    }

    public List<Book> getLibros() {
        return libros;
    }

    public void setLibros(List<Book> libros) {
        this.libros = libros;
    }

    @Override
    public Author copiar() {
        Author copia = new Author(this.id, this.nombres, this.apellidos);
        List<Book> copiasLibros = new ArrayList<>();
        for (Book libro : this.libros) {
            copiasLibros.add(libro.copiar());
        }
        copia.setLibros(copiasLibros);
        return copia;
    }
}
