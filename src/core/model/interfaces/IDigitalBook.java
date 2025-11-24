package core.model.interfaces;

public interface IDigitalBook extends IBook {
    String getEnlaceDescarga();

    void setEnlaceDescarga(String enlaceDescarga);

    boolean tieneEnlace();

    IDigitalBook copiar();
}
