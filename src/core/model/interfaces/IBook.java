package core.model.interfaces;

import java.util.List;

public interface IBook {
    String getTitulo();

    void setTitulo(String titulo);

    List<IAuthor> getAutores();

    void setAutores(List<IAuthor> autores);

    String getIsbn();

    void setIsbn(String isbn);

    String getGenero();

    void setGenero(String genero);

    String getFormato();

    void setFormato(String formato);

    double getValor();

    void setValor(double valor);

    IPublisher getEditorial();

    void setEditorial(IPublisher editorial);

    IBook copiar();
}
