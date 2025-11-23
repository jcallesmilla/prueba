package core.controller;

import core.model.Author;
import core.model.Manager;
import core.model.Narrator;
import core.model.storage.PersonStorage;
import core.util.Response;
import core.util.StatusCode;
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

    public Response<Author> crearAutor(String idTexto, String nombres, String apellidos) {
        if (!camposNombreValidos(nombres, apellidos) || idTexto == null || idTexto.isEmpty()) {
            return new Response<>(StatusCode.ERROR_VALIDACION, "Todos los campos del autor son obligatorios.");
        }
        if (!validarId(idTexto)) {
            return new Response<>(StatusCode.ERROR_VALIDACION, "El ID del autor debe ser numérico, mayor o igual a 0 y con máximo 15 dígitos.");
        }
        long id = convertirId(idTexto);
        if (personStorage.existeId(id)) {
            return new Response<>(StatusCode.ERROR_DUPLICADO, "Ya existe una persona con ese ID.");
        }
        Author autor = new Author(id, nombres, apellidos);
        personStorage.guardarAutor(autor);
        return new Response<>(StatusCode.SUCCESS, "Autor creado correctamente.", autor);
    }

    public Response<Manager> crearGerente(String idTexto, String nombres, String apellidos) {
        if (!camposNombreValidos(nombres, apellidos) || idTexto == null || idTexto.isEmpty()) {
            return new Response<>(StatusCode.ERROR_VALIDACION, "Todos los campos del gerente son obligatorios.");
        }
        if (!validarId(idTexto)) {
            return new Response<>(StatusCode.ERROR_VALIDACION, "El ID del gerente debe ser numérico, mayor o igual a 0 y con máximo 15 dígitos.");
        }
        long id = convertirId(idTexto);
        if (personStorage.existeId(id)) {
            return new Response<>(StatusCode.ERROR_DUPLICADO, "Ya existe una persona con ese ID.");
        }
        Manager gerente = new Manager(id, nombres, apellidos);
        personStorage.guardarGerente(gerente);
        return new Response<>(StatusCode.SUCCESS, "Gerente creado correctamente.", gerente);
    }

    public Response<Narrator> crearNarrador(String idTexto, String nombres, String apellidos) {
        if (!camposNombreValidos(nombres, apellidos) || idTexto == null || idTexto.isEmpty()) {
            return new Response<>(StatusCode.ERROR_VALIDACION, "Todos los campos del narrador son obligatorios.");
        }
        if (!validarId(idTexto)) {
            return new Response<>(StatusCode.ERROR_VALIDACION, "El ID del narrador debe ser numérico, mayor o igual a 0 y con máximo 15 dígitos.");
        }
        long id = convertirId(idTexto);
        if (personStorage.existeId(id)) {
            return new Response<>(StatusCode.ERROR_DUPLICADO, "Ya existe una persona con ese ID.");
        }
        Narrator narrador = new Narrator(id, nombres, apellidos);
        personStorage.guardarNarrador(narrador);
        return new Response<>(StatusCode.SUCCESS, "Narrador creado correctamente.", narrador);
    }

    public Response<List<Author>> obtenerAutores() {
        List<Author> copias = new ArrayList<>();
        for (Author autor : personStorage.obtenerAutoresOrdenados()) {
            copias.add(autor.copiar());
        }
        return new Response<>(StatusCode.SUCCESS, "Autores listados.", copias);
    }

    public Response<List<Manager>> obtenerGerentes() {
        List<Manager> copias = new ArrayList<>();
        for (Manager gerente : personStorage.obtenerGerentesOrdenados()) {
            copias.add(gerente.copiar());
        }
        return new Response<>(StatusCode.SUCCESS, "Gerentes listados.", copias);
    }

    public Response<List<Narrator>> obtenerNarradores() {
        List<Narrator> copias = new ArrayList<>();
        for (Narrator narrador : personStorage.obtenerNarradoresOrdenados()) {
            copias.add(narrador.copiar());
        }
        return new Response<>(StatusCode.SUCCESS, "Narradores listados.", copias);
    }

    public Author buscarAutorPorId(long id) {
        return personStorage.buscarAutor(id);
    }

    public Manager buscarGerentePorId(long id) {
        return personStorage.buscarGerente(id);
    }

    public Narrator buscarNarradorPorId(long id) {
        return personStorage.buscarNarrador(id);
    }
}
