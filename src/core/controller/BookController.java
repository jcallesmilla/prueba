package core.controller;

import core.model.Audiobook;
import core.model.DigitalBook;
import core.model.PrintedBook;
import core.model.interfaces.IAudiobook;
import core.model.interfaces.IAuthor;
import core.model.interfaces.IBook;
import core.model.interfaces.IDigitalBook;
import core.model.interfaces.INarrator;
import core.model.interfaces.IPublisher;
import core.model.interfaces.IPrintedBook;
import core.model.storage.BookStorage;
import core.model.storage.PersonStorage;
import core.model.storage.PublisherStorage;
import core.controller.util.Response;
import core.controller.util.Status;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class BookController {

    private final BookStorage bookStorage;
    private final PersonStorage personStorage;
    private final PublisherStorage publisherStorage;

    public BookController(BookStorage bookStorage, PersonStorage personStorage, PublisherStorage publisherStorage) {
        this.bookStorage = bookStorage;
        this.personStorage = personStorage;
        this.publisherStorage = publisherStorage;
    }

    public Response<List<String>> agregarAutorALaLista(List<String> actuales, String autorTexto) {
        if (autorTexto == null || autorTexto.isEmpty()) {
            return new Response<>(Status.BAD_REQUEST, "Debes seleccionar un autor para agregar.");
        }
        List<String> nuevaLista = new ArrayList<>(actuales);
        if (nuevaLista.contains(autorTexto)) {
            return new Response<>(Status.BAD_REQUEST, "Ese autor ya está en la lista.", nuevaLista);
        }
        nuevaLista.add(autorTexto);
        return new Response<>(Status.OK, "Autor agregado a la lista.", nuevaLista);
    }

    public Response<List<String>> eliminarAutorDeLaLista(List<String> actuales, String autorTexto) {
        List<String> nuevaLista = new ArrayList<>(actuales);
        nuevaLista.remove(autorTexto);
        return new Response<>(Status.OK, "Autor removido.", nuevaLista);
    }

    public Response<List<String>> agregarStandALista(List<String> actuales, String standId) {
        if (standId == null || standId.isEmpty()) {
            return new Response<>(Status.BAD_REQUEST, "Debes seleccionar un stand.");
        }
        List<String> nueva = new ArrayList<>(actuales);
        if (nueva.contains(standId)) {
            return new Response<>(Status.BAD_REQUEST, "Ese stand ya fue agregado.", nueva);
        }
        nueva.add(standId);
        return new Response<>(Status.OK, "Stand agregado.", nueva);
    }

    public Response<List<String>> eliminarStandDeLista(List<String> actuales, String standId) {
        List<String> nueva = new ArrayList<>(actuales);
        nueva.remove(standId);
        return new Response<>(Status.OK, "Stand removido.", nueva);
    }

    public Response<List<String>> agregarEditorialALista(List<String> actuales, String editorialTexto) {
        if (editorialTexto == null || editorialTexto.isEmpty()) {
            return new Response<>(Status.BAD_REQUEST, "Debes seleccionar una editorial.");
        }
        List<String> nueva = new ArrayList<>(actuales);
        if (nueva.contains(editorialTexto)) {
            return new Response<>(Status.BAD_REQUEST, "La editorial ya está en la lista.", nueva);
        }
        nueva.add(editorialTexto);
        return new Response<>(Status.OK, "Editorial agregada.", nueva);
    }

    public Response<List<String>> eliminarEditorialDeLista(List<String> actuales, String editorialTexto) {
        List<String> nueva = new ArrayList<>(actuales);
        nueva.remove(editorialTexto);
        return new Response<>(Status.OK, "Editorial removida.", nueva);
    }

    private boolean isbnValido(String isbn) {
        return isbn != null && isbn.matches("\\d{3}-\\d-\\d{2}-\\d{6}-\\d");
    }

    private boolean valorValido(String valorTexto) {
        try {
            double valor = Double.parseDouble(valorTexto);
            return valor > 0;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private double convertirValor(String valorTexto) {
        return Double.parseDouble(valorTexto);
    }

    private List<IAuthor> convertirAutores(List<String> autoresTexto) {
        List<IAuthor> autores = new ArrayList<>();
        for (String linea : autoresTexto) {
            if (linea == null || linea.isEmpty()) {
                continue;
            }
            String[] partes = linea.split(" - ");
            if (partes.length == 0) {
                continue;
            }
            try {
                long id = Long.parseLong(partes[0]);
                IAuthor autor = personStorage.buscarAutor(id);
                if (autor != null) {
                    autores.add(autor);
                }
            } catch (NumberFormatException e) {
                // se ignora formato incorrecto
            }
        }
        return autores;
    }

    private Response<List<IAuthor>> validarAutores(List<String> autoresTexto) {
        if (autoresTexto == null || autoresTexto.isEmpty()) {
            return new Response<>(Status.BAD_REQUEST, "Debes agregar al menos un autor.");
        }
        List<IAuthor> autores = convertirAutores(autoresTexto);
        if (autores.isEmpty()) {
            return new Response<>(Status.NOT_FOUND, "Los autores seleccionados no existen.");
        }
        Set<Long> ids = new HashSet<>();
        for (IAuthor autor : autores) {
            if (ids.contains(autor.getId())) {
                return new Response<>(Status.BAD_REQUEST, "No se permiten autores repetidos en el libro.");
            }
            ids.add(autor.getId());
        }
        return new Response<>(Status.OK, "Autores validados.", autores);
    }

    private IPublisher obtenerEditorialDesdeCombo(String textoEditorial) {
        if (textoEditorial == null || textoEditorial.isEmpty()) {
            return null;
        }
        if (!textoEditorial.contains("(")) {
            return publisherStorage.buscarPorNit(textoEditorial);
        }
        String nit = textoEditorial.substring(textoEditorial.indexOf("(") + 1, textoEditorial.indexOf(")"));
        return publisherStorage.buscarPorNit(nit);
    }

    public Response<IBook> crearLibroImpreso(String titulo, List<String> autoresTexto, String isbn, String genero,
            String formato, String valorTexto, String textoEditorial, String paginasTexto, String copiasTexto) {
        Response<List<IAuthor>> validacionAutores = validarAutores(autoresTexto);
        if (validacionAutores.getCodigo() != Status.OK) {
            return new Response<>(validacionAutores.getCodigo(), validacionAutores.getMensaje());
        }
        if (titulo == null || titulo.isEmpty() || genero == null || genero.isEmpty() || formato == null
                || formato.isEmpty()
                || valorTexto == null || valorTexto.isEmpty() || paginasTexto == null || paginasTexto.isEmpty()
                || copiasTexto == null || copiasTexto.isEmpty()) {
            return new Response<>(Status.BAD_REQUEST, "Todos los campos del libro impreso son obligatorios.");
        }
        if (!isbnValido(isbn)) {
            return new Response<>(Status.BAD_REQUEST, "El ISBN debe tener el formato XXX-X-XX-XXXXXX-X.");
        }
        if (!valorValido(valorTexto)) {
            return new Response<>(Status.BAD_REQUEST, "El valor del libro debe ser mayor que 0.");
        }
        if (bookStorage.existeIsbn(isbn)) {
            return new Response<>(Status.BAD_REQUEST, "Ya existe un libro con ese ISBN.");
        }
        int paginas;
        int copias;
        try {
            paginas = Integer.parseInt(paginasTexto);
            copias = Integer.parseInt(copiasTexto);
        } catch (NumberFormatException e) {
            return new Response<>(Status.BAD_REQUEST, "Páginas y copias deben ser números enteros.");
        }
        if (paginas <= 0 || copias <= 0) {
            return new Response<>(Status.BAD_REQUEST, "Páginas y copias deben ser mayores que 0.");
        }
        IPublisher editorial = obtenerEditorialDesdeCombo(textoEditorial);
        if (editorial == null) {
            return new Response<>(Status.NOT_FOUND, "La editorial seleccionada no existe.");
        }
        double valor = convertirValor(valorTexto);
        List<IAuthor> autores = validacionAutores.getDato();
        IPrintedBook libro = new PrintedBook(titulo, autores, isbn, genero, formato, valor, editorial, paginas, copias);
        registrarRelaciones(libro, autores, editorial, null);
        bookStorage.guardar(libro);
        return new Response<>(Status.CREATED, "Libro impreso creado correctamente.", libro);
    }

    public Response<IBook> crearLibroDigital(String titulo, List<String> autoresTexto, String isbn, String genero,
            String formato, String valorTexto, String textoEditorial, String enlace) {
        Response<List<IAuthor>> validacionAutores = validarAutores(autoresTexto);
        if (validacionAutores.getCodigo() != Status.OK) {
            return new Response<>(validacionAutores.getCodigo(), validacionAutores.getMensaje());
        }
        if (titulo == null || titulo.isEmpty() || genero == null || genero.isEmpty() || formato == null
                || formato.isEmpty()
                || valorTexto == null || valorTexto.isEmpty()) {
            return new Response<>(Status.BAD_REQUEST, "Todos los campos del libro digital son obligatorios.");
        }
        if (!isbnValido(isbn)) {
            return new Response<>(Status.BAD_REQUEST, "El ISBN debe tener el formato XXX-X-XX-XXXXXX-X.");
        }
        if (!valorValido(valorTexto)) {
            return new Response<>(Status.BAD_REQUEST, "El valor del libro debe ser mayor que 0.");
        }
        if (bookStorage.existeIsbn(isbn)) {
            return new Response<>(Status.BAD_REQUEST, "Ya existe un libro con ese ISBN.");
        }
        IPublisher editorial = obtenerEditorialDesdeCombo(textoEditorial);
        if (editorial == null) {
            return new Response<>(Status.NOT_FOUND, "La editorial seleccionada no existe.");
        }
        double valor = convertirValor(valorTexto);
        List<IAuthor> autores = validacionAutores.getDato();
        IDigitalBook libro = new DigitalBook(titulo, autores, isbn, genero, formato, valor, editorial, enlace);
        registrarRelaciones(libro, autores, editorial, null);
        bookStorage.guardar(libro);
        return new Response<>(Status.CREATED, "Libro digital creado correctamente.", libro);
    }

    public Response<IBook> crearAudiolibro(String titulo, List<String> autoresTexto, String isbn, String genero,
            String formato, String valorTexto, String textoEditorial, String duracionTexto, String idNarradorTexto) {
        Response<List<IAuthor>> validacionAutores = validarAutores(autoresTexto);
        if (validacionAutores.getCodigo() != Status.OK) {
            return new Response<>(validacionAutores.getCodigo(), validacionAutores.getMensaje());
        }
        if (titulo == null || titulo.isEmpty() || genero == null || genero.isEmpty() || formato == null
                || formato.isEmpty()
                || valorTexto == null || valorTexto.isEmpty() || duracionTexto == null || duracionTexto.isEmpty()
                || idNarradorTexto == null || idNarradorTexto.isEmpty()) {
            return new Response<>(Status.BAD_REQUEST, "Todos los campos del audiolibro son obligatorios.");
        }
        if (!isbnValido(isbn)) {
            return new Response<>(Status.BAD_REQUEST, "El ISBN debe tener el formato XXX-X-XX-XXXXXX-X.");
        }
        if (!valorValido(valorTexto)) {
            return new Response<>(Status.BAD_REQUEST, "El valor del libro debe ser mayor que 0.");
        }
        if (bookStorage.existeIsbn(isbn)) {
            return new Response<>(Status.BAD_REQUEST, "Ya existe un libro con ese ISBN.");
        }
        int duracion;
        try {
            duracion = Integer.parseInt(duracionTexto);
        } catch (NumberFormatException e) {
            return new Response<>(Status.BAD_REQUEST, "La duración debe ser un número entero.");
        }
        if (duracion <= 0) {
            return new Response<>(Status.BAD_REQUEST, "La duración debe ser mayor que 0.");
        }
        IPublisher editorial = obtenerEditorialDesdeCombo(textoEditorial);
        if (editorial == null) {
            return new Response<>(Status.NOT_FOUND, "La editorial seleccionada no existe.");
        }
        long idNarrador;
        try {
            idNarrador = Long.parseLong(idNarradorTexto);
        } catch (NumberFormatException e) {
            return new Response<>(Status.BAD_REQUEST, "El narrador debe tener un ID numérico.");
        }
        INarrator narrador = personStorage.buscarNarrador(idNarrador);
        if (narrador == null) {
            return new Response<>(Status.NOT_FOUND, "El narrador seleccionado no existe.");
        }
        double valor = convertirValor(valorTexto);
        List<IAuthor> autores = validacionAutores.getDato();
        IAudiobook libro = new Audiobook(titulo, autores, isbn, genero, formato, valor, editorial, duracion, narrador);
        registrarRelaciones(libro, autores, editorial, narrador);
        bookStorage.guardar(libro);
        return new Response<>(Status.CREATED, "Audiolibro creado correctamente.", libro);
    }

    private void registrarRelaciones(IBook libro, List<IAuthor> autores, IPublisher editorial, INarrator narrador) {
        for (IAuthor autor : autores) {
            List<IBook> librosAutor = autor.getLibros();
            librosAutor.add(libro);
            autor.setLibros(librosAutor);
        }
        List<IBook> librosEditorial = editorial.getLibros();
        librosEditorial.add(libro);
        editorial.setLibros(librosEditorial);
        if (narrador != null) {
            List<IBook> librosNarrador = narrador.getAudiolibros();
            librosNarrador.add(libro);
            narrador.setAudiolibros(librosNarrador);
        }
    }

    public Response<List<IBook>> obtenerLibros() {
        List<IBook> copias = new ArrayList<>();
        for (IBook libro : bookStorage.obtenerOrdenados()) {
            copias.add(libro.copiar());
        }
        return new Response<>(Status.OK, "Libros listados.", copias);
    }

    public Response<List<IBook>> obtenerLibrosPorAutor(long idAutor) {
        IAuthor autor = personStorage.buscarAutor(idAutor);
        if (autor == null) {
            return new Response<>(Status.NOT_FOUND, "El autor no existe.");
        }
        List<IBook> copias = new ArrayList<>();
        for (IBook libro : autor.getLibros()) {
            copias.add(libro.copiar());
        }
        // Ordenar por ISBN
        copias.sort(Comparator.comparing(IBook::getIsbn));
        return new Response<>(Status.OK, "Libros del autor listados.", copias);
    }

    public Response<List<IBook>> obtenerLibrosPorFormato(String formato) {
        List<IBook> copias = new ArrayList<>();
        for (IBook libro : bookStorage.obtenerOrdenados()) {
            if (libro.getFormato().equals(formato)) {
                copias.add(libro.copiar());
            }
        }
        return new Response<>(Status.OK, "Libros filtrados por formato.", copias);
    }

    public Response<List<IAuthor>> autoresConMasEditoriales() {
        List<IAuthor> autores = personStorage.obtenerAutoresOrdenados();
        int maximo = -1;
        List<IAuthor> resultado = new ArrayList<>();
        for (IAuthor autor : autores) {
            Set<String> editoriales = new HashSet<>();
            for (IBook libro : autor.getLibros()) {
                if (libro.getEditorial() != null) {
                    editoriales.add(libro.getEditorial().getNit());
                }
            }
            if (editoriales.size() > maximo) {
                maximo = editoriales.size();
                resultado.clear();
                resultado.add(autor);
            } else if (editoriales.size() == maximo) {
                resultado.add(autor);
            }
        }
        List<IAuthor> copias = new ArrayList<>();
        for (IAuthor autor : resultado) {
            copias.add((IAuthor) autor.copiar());
        }
        return new Response<>(Status.OK, "Autores con más editoriales listados.", copias);
    }
}
