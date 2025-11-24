package core.model.interfaces;

import java.util.List;

public interface IStand {
    long getId();

    void setId(long id);

    double getPrecio();

    void setPrecio(double precio);

    List<IPublisher> getEditoriales();

    void setEditoriales(List<IPublisher> editoriales);

    IStand copiar();
}
