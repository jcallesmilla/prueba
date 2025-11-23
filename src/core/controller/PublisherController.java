package core.controller;

import core.model.Manager;
import core.model.Publisher;
import core.model.storage.PersonStorage;
import core.model.storage.PublisherStorage;
import core.util.Response;
import core.util.StatusCode;
import java.util.ArrayList;
import java.util.List;


public class PublisherController {

    private final PublisherStorage publisherStorage;
    private final PersonStorage personStorage;

    public PublisherController(PublisherStorage publisherStorage, PersonStorage personStorage) {
        this.publisherStorage = publisherStorage;
        this.personStorage= personStorage;
    }

    public Response<Publisher> crearEditorial(String nit, String nombre, String direccion, String idGerenteTexto) {
        if (nit == null || nombre == null || direccion == null || idGerenteTexto == null
                || nit.isEmpty() || nombre.isEmpty() || direccion.isEmpty() || idGerenteTexto.isEmpty()) {
            return new Response<>(StatusCode.ERROR_VALIDACION, "Todos los campos de la editorial son obligatorios.");
        }
        if (!nit.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d")) {
            return new Response<>(StatusCode.ERROR_VALIDACION, "El NIT debe tener el formato XXX.XXX.XXX-X.");
        }
        long idGerente;
        try {
            idGerente = Long.parseLong(idGerenteTexto);
        } catch (NumberFormatException e) {
            return new Response<>(StatusCode.ERROR_VALIDACION, "El ID del gerente debe ser numérico.");
        }
        if (publisherStorage.existeNit(nit)) {
            return new Response<>(StatusCode.ERROR_DUPLICADO, "Ya existe una editorial con ese NIT.");
        }
        Manager gerente = personStorage.buscarGerente(idGerente);
        if (gerente == null) {
            return new Response<>(StatusCode.ERROR_NO_ENCONTRADO, "El gerente seleccionado no existe.");
        }
        if (gerente.getEditorial() != null) {
            return new Response<>(StatusCode.ERROR_VALIDACION, "El gerente ya está asignado a otra editorial.");
        }
        Publisher editorial = new Publisher(nit, nombre, direccion, gerente);
        gerente.setEditorial(editorial);
        publisherStorage.guardar(editorial);
        return new Response<>(StatusCode.SUCCESS, "Editorial creada correctamente.", editorial);
    }

    public Response<List<Publisher>> obtenerEditoriales() {
        List<Publisher> copias = new ArrayList<>();
        for (Publisher editorial : publisherStorage.obtenerOrdenados()) {
            copias.add(editorial.copiar());
        }
        return new Response<>(StatusCode.SUCCESS, "Editoriales listadas.", copias);
    }

    public Publisher buscarPorNit(String nit) {
        return publisherStorage.buscarPorNit(nit);
    }
}
