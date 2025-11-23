package core.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un narrador.
 */
public class Narrator extends Person {

    private List<Book> audiolibros;

    public Narrator(long id, String nombres, String apellidos) {
        super(id, nombres, apellidos);
        this.audiolibros = new ArrayList<>();
    }

    public List<Book> getAudiolibros() {
        return audiolibros;
    }

    public void setAudiolibros(List<Book> audiolibros) {
        this.audiolibros = audiolibros;
    }

    @Override
    public Narrator copiar() {
        Narrator copia = new Narrator(this.id, this.nombres, this.apellidos);
        List<Book> copias = new ArrayList<>();
        for (Book libro : this.audiolibros) {
            copias.add(libro.copiar());
        }
        copia.setAudiolibros(copias);
        return copia;
    }
}
