package core.util;

/**
 * Respuesta genérica para las operaciones de los controladores.
 * @param <T> tipo de dato a retornar.
 */
public class Response<T> {

    private StatusCode codigo;
    private String mensaje;
    private T dato;

    public Response(StatusCode codigo, String mensaje) {
        this.codigo = codigo;
        this.mensaje = mensaje;
    }

    public Response(StatusCode codigo, String mensaje, T dato) {
        this.codigo = codigo;
        this.mensaje = mensaje;
        this.dato = dato;
    }

    public StatusCode getCodigo() {
        return codigo;
    }

    public void setCodigo(StatusCode codigo) {
        this.codigo = codigo;
    }

    public String getMensaje() {
        return mensaje;
    }

    public void setMensaje(String mensaje) {
        this.mensaje = mensaje;
    }

    public T getDato() {
        return dato;
    }

    public void setDato(T dato) {
        this.dato = dato;
    }
}
