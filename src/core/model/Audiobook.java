package core.model;

import java.util.List;

/**
 * Audiolibro.
 */
public class Audiobook extends Book {

    private int duracion;
    private Narrator narrador;

    public Audiobook(String titulo, List<Author> autores, String isbn, String genero, String formato, double valor, Publisher editorial, int duracion, Narrator narrador) {
        super(titulo, autores, isbn, genero, formato, valor, editorial);
        this.duracion = duracion;
        this.narrador = narrador;
    }

    public int getDuracion() {
        return duracion;
    }

    public void setDuracion(int duracion) {
        this.duracion = duracion;
    }

    public Narrator getNarrador() {
        return narrador;
    }

    public void setNarrador(Narrator narrador) {
        this.narrador = narrador;
    }

    @Override
    public Audiobook copiar() {
        Narrator narradorCopia = null;
        if (this.narrador != null) {
            narradorCopia = new Narrator(this.narrador.getId(), this.narrador.getNombres(), this.narrador.getApellidos());
        }
        return new Audiobook(this.titulo, copiarAutores(), this.isbn, this.genero, this.formato, this.valor, copiarEditorial(), this.duracion, narradorCopia);
    }
}
