package core.model.interfaces;

import java.util.List;

public interface INarrator extends IPerson {
    List<IBook> getAudiolibros();

    void setAudiolibros(List<IBook> audiolibros);

    INarrator copiar();
}
