package core.model.interfaces;

public interface IPrintedBook extends IBook {
    int getPaginas();

    void setPaginas(int paginas);

    int getCopias();

    void setCopias(int copias);

    IPrintedBook copiar();
}
