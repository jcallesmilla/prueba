package core.controller;

import core.model.Author;
import core.model.Manager;
import core.model.Narrator;
import core.model.interfaces.IAuthor;
import core.model.interfaces.IManager;
import core.model.interfaces.INarrator;
import core.model.interfaces.IPerson;
import core.model.storage.PersonStorage;
import core.controller.util.Response;
import core.controller.util.Status;
import java.util.ArrayList;
import java.util.List;

public class PersonController {

    private final PersonStorage personStorage;

    public PersonController(PersonStorage personStorage) {
        this.personStorage = personStorage;
    }

    private boolean validarId(String idTexto) {
        try {
            long id = Long.parseLong(idTexto);
            return id >= 0 && String.valueOf(id).length() <= 15;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private long convertirId(String idTexto) {
        return Long.parseLong(idTexto);
    }

    private boolean camposNombreValidos(String nombres, String apellidos) {
        return nombres != null && apellidos != null && !nombres.isEmpty() && !apellidos.isEmpty();
    }

    public Response<IAuthor> crearAutor(String idTexto, String nombres, String apellidos) {
        if (!camposNombreValidos(nombres, apellidos) || idTexto == null || idTexto.isEmpty()) {
            return new Response<>(Status.BAD_REQUEST, "Todos los campos del autor son obligatorios.");
        }
        if (!validarId(idTexto)) {
            return new Response<>(Status.BAD_REQUEST,
                    "El ID del autor debe ser numérico, mayor o igual a 0 y con máximo 15 dígitos.");
        }
        long id = convertirId(idTexto);
        if (personStorage.existeId(id)) {
            return new Response<>(Status.BAD_REQUEST, "Ya existe una persona con ese ID.");
        }
        IAuthor autor = new Author(id, nombres, apellidos);
        personStorage.guardarAutor(autor);
        return new Response<>(Status.CREATED, "Autor creado correctamente.", autor);
    }

    public Response<IManager> crearGerente(String idTexto, String nombres, String apellidos) {
        if (!camposNombreValidos(nombres, apellidos) || idTexto == null || idTexto.isEmpty()) {
            return new Response<>(Status.BAD_REQUEST, "Todos los campos del gerente son obligatorios.");
        }
        if (!validarId(idTexto)) {
            return new Response<>(Status.BAD_REQUEST,
                    "El ID del gerente debe ser numérico, mayor o igual a 0 y con máximo 15 dígitos.");
        }
        long id = convertirId(idTexto);
        if (personStorage.existeId(id)) {
            return new Response<>(Status.BAD_REQUEST, "Ya existe una persona con ese ID.");
        }
        IManager gerente = new Manager(id, nombres, apellidos);
        personStorage.guardarGerente(gerente);
        return new Response<>(Status.CREATED, "Gerente creado correctamente.", gerente);
    }

    public Response<INarrator> crearNarrador(String idTexto, String nombres, String apellidos) {
        if (!camposNombreValidos(nombres, apellidos) || idTexto == null || idTexto.isEmpty()) {
            return new Response<>(Status.BAD_REQUEST, "Todos los campos del narrador son obligatorios.");
        }
        if (!validarId(idTexto)) {
            return new Response<>(Status.BAD_REQUEST,
                    "El ID del narrador debe ser numérico, mayor o igual a 0 y con máximo 15 dígitos.");
        }
        long id = convertirId(idTexto);
        if (personStorage.existeId(id)) {
            return new Response<>(Status.BAD_REQUEST, "Ya existe una persona con ese ID.");
        }
        INarrator narrador = new Narrator(id, nombres, apellidos);
        personStorage.guardarNarrador(narrador);
        return new Response<>(Status.CREATED, "Narrador creado correctamente.", narrador);
    }

    public Response<List<IAuthor>> obtenerAutores() {
        List<IAuthor> copias = new ArrayList<>();
        for (IAuthor autor : personStorage.obtenerAutoresOrdenados()) {
            copias.add((IAuthor) autor.copiar());
        }
        return new Response<>(Status.OK, "Autores listados.", copias);
    }

    public Response<List<IManager>> obtenerGerentes() {
        List<IManager> copias = new ArrayList<>();
        for (IManager gerente : personStorage.obtenerGerentesOrdenados()) {
            copias.add((IManager) gerente.copiar());
        }
        return new Response<>(Status.OK, "Gerentes listados.", copias);
    }

    public Response<List<INarrator>> obtenerNarradores() {
        List<INarrator> copias = new ArrayList<>();
        for (INarrator narrador : personStorage.obtenerNarradoresOrdenados()) {
            copias.add((INarrator) narrador.copiar());
        }
        return new Response<>(Status.OK, "Narradores listados.", copias);
    }

    public Response<List<IPerson>> obtenerTodasLasPersonas() {
        List<IPerson> copias = new ArrayList<>();
        for (IPerson persona : personStorage.obtenerTodasLasPersonasOrdenadas()) {
            copias.add(persona.copiar());
        }
        return new Response<>(Status.OK, "Personas listadas.", copias);
    }

    public IAuthor buscarAutorPorId(long id) {
        return personStorage.buscarAutor(id);
    }

    public IManager buscarGerentePorId(long id) {
        return personStorage.buscarGerente(id);
    }

    public INarrator buscarNarradorPorId(long id) {
        return personStorage.buscarNarrador(id);
    }
}
