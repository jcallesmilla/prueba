package core.model;

import core.model.interfaces.INarrator;
import core.model.interfaces.IBook;
import java.util.ArrayList;
import java.util.List;

/**
 * Representa a un narrador.
 */
public class Narrator extends Person implements INarrator {

    private List<IBook> audiolibros;

    public Narrator(long id, String nombres, String apellidos) {
        super(id, nombres, apellidos);
        this.audiolibros = new ArrayList<>();
    }

    @Override
    public List<IBook> getAudiolibros() {
        return audiolibros;
    }

    @Override
    public void setAudiolibros(List<IBook> audiolibros) {
        this.audiolibros = audiolibros;
    }

    protected INarrator crearCopiaSimple() {
        return new Narrator(this.id, this.nombres, this.apellidos);
    }

    @Override
    public INarrator copiar() {
        Narrator copia = new Narrator(this.id, this.nombres, this.apellidos);
        List<IBook> copias = new ArrayList<>();
        for (IBook libro : this.audiolibros) {
            copias.add(libro.copiar());
        }
        copia.setAudiolibros(copias);
        return copia;
    }
}
