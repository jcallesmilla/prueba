package core.model;

import core.model.interfaces.IAuthor;
import core.model.interfaces.IBook;
import core.model.interfaces.IPublisher;
import core.model.interfaces.IManager;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase base para los libros.
 */
public abstract class Book implements IBook {

    protected String titulo;
    protected List<IAuthor> autores;
    protected String isbn;
    protected String genero;
    protected String formato;
    protected double valor;
    protected IPublisher editorial;

    public Book(String titulo, List<IAuthor> autores, String isbn, String genero, String formato, double valor,
            IPublisher editorial) {
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

    public List<IAuthor> getAutores() {
        return autores;
    }

    public void setAutores(List<IAuthor> autores) {
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

    public IPublisher getEditorial() {
        return editorial;
    }

    public void setEditorial(IPublisher editorial) {
        this.editorial = editorial;
    }

    protected List<IAuthor> copiarAutores() {
        List<IAuthor> copiasAutores = new ArrayList<>();
        for (IAuthor autor : this.autores) {
            IAuthor autorCopia = new Author(autor.getId(), autor.getNombres(), autor.getApellidos());
            copiasAutores.add(autorCopia);
        }
        return copiasAutores;
    }

    protected IPublisher copiarEditorial() {
        if (this.editorial == null) {
            return null;
        }
        IPublisher copia = new Publisher(this.editorial.getNit(), this.editorial.getNombre(),
                this.editorial.getDireccion(), null);
        if (this.editorial.getGerente() != null) {
            IManager gerente = this.editorial.getGerente();
            copia.setGerente(new Manager(gerente.getId(), gerente.getNombres(), gerente.getApellidos()));
        }
        return copia;
    }

    protected IAuthor crearAutorSimple(IAuthor original) {
        return new Author(original.getId(), original.getNombres(), original.getApellidos());
    }

    protected IManager crearGerenteSimple(IManager original) {
        return new Manager(original.getId(), original.getNombres(), original.getApellidos());
    }

    public abstract IBook copiar();
}
