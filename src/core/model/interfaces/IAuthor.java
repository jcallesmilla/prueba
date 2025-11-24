package core.model.interfaces;

import java.util.List;

public interface IAuthor extends IPerson {
    List<IBook> getLibros();

    void setLibros(List<IBook> libros);

    IAuthor copiar();
}
