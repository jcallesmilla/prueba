package core.model;

import java.util.List;
import core.model.interfaces.IPrintedBook;
import core.model.interfaces.IAuthor;
import core.model.interfaces.IPublisher;

/**
 * Libro impreso.
 */
public class PrintedBook extends Book implements IPrintedBook {

    private int paginas;
    private int copias;

    public PrintedBook(String titulo, List<IAuthor> autores, String isbn, String genero, String formato, double valor,
            IPublisher editorial, int paginas, int copias) {
        super(titulo, autores, isbn, genero, formato, valor, editorial);
        this.paginas = paginas;
        this.copias = copias;
    }

    @Override
    public int getPaginas() {
        return paginas;
    }

    @Override
    public void setPaginas(int paginas) {
        this.paginas = paginas;
    }

    @Override
    public int getCopias() {
        return copias;
    }

    @Override
    public void setCopias(int copias) {
        this.copias = copias;
    }

    @Override
    public IPrintedBook copiar() {
        return new PrintedBook(this.titulo, this.copiarAutores(), this.isbn, this.genero, this.formato, this.valor,
                this.copiarEditorial(), this.paginas, this.copias);
    }
}
