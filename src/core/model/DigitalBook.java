package core.model;

import java.util.List;

/**
 * Libro digital.
 */
public class DigitalBook extends Book {

    private String enlaceDescarga;

    public DigitalBook(String titulo, List<Author> autores, String isbn, String genero, String formato, double valor, Publisher editorial, String enlaceDescarga) {
        super(titulo, autores, isbn, genero, formato, valor, editorial);
        this.enlaceDescarga = enlaceDescarga;
    }

    public String getEnlaceDescarga() {
        return enlaceDescarga;
    }

    public void setEnlaceDescarga(String enlaceDescarga) {
        this.enlaceDescarga = enlaceDescarga;
    }

    public boolean tieneEnlace() {
        return enlaceDescarga != null && !enlaceDescarga.isEmpty();
    }

    @Override
    public DigitalBook copiar() {
        return new DigitalBook(this.titulo, copiarAutores(), this.isbn, this.genero, this.formato, this.valor, copiarEditorial(), this.enlaceDescarga);
    }
}
