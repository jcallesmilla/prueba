package core.model;

import java.util.List;

/**
 * Libro impreso.
 */
public class PrintedBook extends Book {

    private int paginas;
    private int copias;

    public PrintedBook(String titulo, List<Author> autores, String isbn, String genero, String formato, double valor, Publisher editorial, int paginas, int copias) {
        super(titulo, autores, isbn, genero, formato, valor, editorial);
        this.paginas = paginas;
        this.copias = copias;
    }

    public int getPaginas() {
        return paginas;
    }

    public void setPaginas(int paginas) {
        this.paginas = paginas;
    }

    public int getCopias() {
        return copias;
    }

    public void setCopias(int copias) {
        this.copias = copias;
    }

    @Override
    public PrintedBook copiar() {
        return new PrintedBook(this.titulo, copiarAutores(), this.isbn, this.genero, this.formato, this.valor, copiarEditorial(), this.paginas, this.copias);
    }
}
