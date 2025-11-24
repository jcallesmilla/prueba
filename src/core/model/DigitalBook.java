package core.model;

import core.model.interfaces.IDigitalBook;
import core.model.interfaces.IAuthor;
import core.model.interfaces.IPublisher;
import java.util.List;

/**
 * Libro digital.
 */
public class DigitalBook extends Book implements IDigitalBook {

    private String enlaceDescarga;

    public DigitalBook(String titulo, List<IAuthor> autores, String isbn, String genero, String formato, double valor,
            IPublisher editorial, String enlaceDescarga) {
        super(titulo, autores, isbn, genero, formato, valor, editorial);
        this.enlaceDescarga = enlaceDescarga;
    }

    @Override
    public String getEnlaceDescarga() {
        return enlaceDescarga;
    }

    @Override
    public void setEnlaceDescarga(String enlaceDescarga) {
        this.enlaceDescarga = enlaceDescarga;
    }

    @Override
    public boolean tieneEnlace() {
        return this.enlaceDescarga != null && !this.enlaceDescarga.isEmpty();
    }

    @Override
    public IDigitalBook copiar() {
        return new DigitalBook(this.titulo, this.copiarAutores(), this.isbn, this.genero, this.formato, this.valor,
                this.copiarEditorial(), this.enlaceDescarga);
    }
}
