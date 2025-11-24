package core.controller;

import core.model.Publisher;
import core.model.interfaces.IManager;
import core.model.interfaces.IPublisher;
import core.model.storage.PersonStorage;
import core.model.storage.PublisherStorage;
import core.controller.util.Response;
import core.controller.util.Status;
import java.util.ArrayList;
import java.util.List;

public class PublisherController {

    private final PublisherStorage publisherStorage;
    private final PersonStorage personStorage;

    public PublisherController(PublisherStorage publisherStorage, PersonStorage personStorage) {
        this.publisherStorage = publisherStorage;
        this.personStorage = personStorage;
    }

    public Response<IPublisher> crearEditorial(String nit, String nombre, String direccion, String idGerenteTexto) {
        if (nit == null || nombre == null || direccion == null || idGerenteTexto == null
                || nit.isEmpty() || nombre.isEmpty() || direccion.isEmpty() || idGerenteTexto.isEmpty()) {
            return new Response<>(Status.BAD_REQUEST, "Todos los campos de la editorial son obligatorios.");
        }
        if (!nit.matches("\\d{3}\\.\\d{3}\\.\\d{3}-\\d")) {
            return new Response<>(Status.BAD_REQUEST, "El NIT debe tener el formato XXX.XXX.XXX-X.");
        }
        long idGerente;
        try {
            idGerente = Long.parseLong(idGerenteTexto);
        } catch (NumberFormatException e) {
            return new Response<>(Status.BAD_REQUEST, "El ID del gerente debe ser numérico.");
        }
        if (publisherStorage.existeNit(nit)) {
            return new Response<>(Status.BAD_REQUEST, "Ya existe una editorial con ese NIT.");
        }
        IManager gerente = personStorage.buscarGerente(idGerente);
        if (gerente == null) {
            return new Response<>(Status.NOT_FOUND, "El gerente seleccionado no existe.");
        }
        if (gerente.getEditorial() != null) {
            return new Response<>(Status.BAD_REQUEST, "El gerente ya está asignado a otra editorial.");
        }
        IPublisher editorial = new Publisher(nit, nombre, direccion, gerente);
        gerente.setEditorial(editorial);
        publisherStorage.guardar(editorial);
        return new Response<>(Status.CREATED, "Editorial creada correctamente.", editorial);
    }

    public Response<List<IPublisher>> obtenerEditoriales() {
        List<IPublisher> copias = new ArrayList<>();
        for (IPublisher editorial : publisherStorage.obtenerOrdenadasPorNit()) {
            copias.add(editorial.copiar());
        }
        return new Response<>(Status.OK, "Editoriales listadas.", copias);
    }

    public IPublisher buscarPorNit(String nit) {
        return publisherStorage.buscarPorNit(nit);
    }
}