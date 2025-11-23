package core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Clase base para los libros.
 */
public abstract class Book {

    protected String titulo;
    protected List<Author> autores;
    protected String isbn;
    protected String genero;
    protected String formato;
    protected double valor;
    protected Publisher editorial;

    public Book(String titulo, List<Author> autores, String isbn, String genero, String formato, double valor, Publisher editorial) {
        this.titulo = titulo;
        this.autores = autores;
        this.isbn = isbn;
        this.genero = genero;
        this.formato = formato;
        this.valor = valor;
        this.editorial = editorial;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public List<Author> getAutores() {
        return autores;
    }

    public void setAutores(List<Author> autores) {
        this.autores = autores;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getGenero() {
        return genero;
    }

    public void setGenero(String genero) {
        this.genero = genero;
    }

    public String getFormato() {
        return formato;
    }

    public void setFormato(String formato) {
        this.formato = formato;
    }

    public double getValor() {
        return valor;
    }

    public void setValor(double valor) {
        this.valor = valor;
    }

    public Publisher getEditorial() {
        return editorial;
    }

    public void setEditorial(Publisher editorial) {
        this.editorial = editorial;
    }

    protected List<Author> copiarAutores() {
        List<Author> copiasAutores = new ArrayList<>();
        for (Author autor : this.autores) {
            Author autorCopia = new Author(autor.getId(), autor.getNombres(), autor.getApellidos());
            copiasAutores.add(autorCopia);
        }
        return copiasAutores;
    }

    protected Publisher copiarEditorial() {
        if (this.editorial == null) {
            return null;
        }
        Publisher copia = new Publisher(this.editorial.getNit(), this.editorial.getNombre(), this.editorial.getDireccion(), null);
        if (this.editorial.getGerente() != null) {
            Manager gerente = this.editorial.getGerente();
            copia.setGerente(new Manager(gerente.getId(), gerente.getNombres(), gerente.getApellidos()));
        }
        return copia;
    }

    public abstract Book copiar();
}
