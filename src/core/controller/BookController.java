package core.controller;

import core.model.Author;
import core.model.Audiobook;
import core.model.Book;
import core.model.DigitalBook;
import core.model.Narrator;
import core.model.PrintedBook;
import core.model.Publisher;
import core.model.storage.BookStorage;
import core.model.storage.PersonStorage;
import core.model.storage.PublisherStorage;
import core.controller.util.Response;
import core.controller.util.Status;
import java.util.ArrayList;
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

    private List<Author> convertirAutores(List<String> autoresTexto) {
        List<Author> autores = new ArrayList<>();
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
                Author autor = personStorage.buscarAutor(id);
                if (autor != null) {
                    autores.add(autor);
                }
            } catch (NumberFormatException e) {
                // se ignora formato incorrecto
            }
        }
        return autores;
    }

    private Response<List<Author>> validarAutores(List<String> autoresTexto) {
        if (autoresTexto == null || autoresTexto.isEmpty()) {
            return new Response<>(Status.BAD_REQUEST, "Debes agregar al menos un autor.");
        }
        List<Author> autores = convertirAutores(autoresTexto);
        if (autores.isEmpty()) {
            return new Response<>(Status.NOT_FOUND, "Los autores seleccionados no existen.");
        }
        Set<Long> ids = new HashSet<>();
        for (Author autor : autores) {
            if (ids.contains(autor.getId())) {
                return new Response<>(Status.BAD_REQUEST, "No se permiten autores repetidos en el libro.");
            }
            ids.add(autor.getId());
        }
        return new Response<>(Status.OK, "Autores validados.", autores);
    }

    private Publisher obtenerEditorialDesdeCombo(String textoEditorial) {
        if (textoEditorial == null || textoEditorial.isEmpty()) {
            return null;
        }
        if (!textoEditorial.contains("(")) {
            return publisherStorage.buscarPorNit(textoEditorial);
        }
        String nit = textoEditorial.substring(textoEditorial.indexOf("(") + 1, textoEditorial.indexOf(")"));
        return publisherStorage.buscarPorNit(nit);
    }

    public Response<Book> crearLibroImpreso(String titulo, List<String> autoresTexto, String isbn, String genero, String formato, String valorTexto, String textoEditorial, String paginasTexto, String copiasTexto) {
        Response<List<Author>> validacionAutores = validarAutores(autoresTexto);
        if (validacionAutores.getCodigo() != Status.OK) {
            return new Response<>(validacionAutores.getCodigo(), validacionAutores.getMensaje());
        }
        if (titulo == null || titulo.isEmpty() || genero == null || genero.isEmpty() || formato == null || formato.isEmpty()
                || valorTexto == null || valorTexto.isEmpty() || paginasTexto == null || paginasTexto.isEmpty() || copiasTexto == null || copiasTexto.isEmpty()) {
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
        Publisher editorial = obtenerEditorialDesdeCombo(textoEditorial);
        if (editorial == null) {
            return new Response<>(Status.NOT_FOUND, "La editorial seleccionada no existe.");
        }
        double valor = convertirValor(valorTexto);
        List<Author> autores = validacionAutores.getDato();
        PrintedBook libro = new PrintedBook(titulo, autores, isbn, genero, formato, valor, editorial, paginas, copias);
        registrarRelaciones(libro, autores, editorial, null);
        bookStorage.guardar(libro);
        return new Response<>(Status.CREATED, "Libro impreso creado correctamente.", libro);
    }

    public Response<Book> crearLibroDigital(String titulo, List<String> autoresTexto, String isbn, String genero, String formato, String valorTexto, String textoEditorial, String enlace) {
        Response<List<Author>> validacionAutores = validarAutores(autoresTexto);
        if (validacionAutores.getCodigo() != Status.OK) {
            return new Response<>(validacionAutores.getCodigo(), validacionAutores.getMensaje());
        }
        if (titulo == null || titulo.isEmpty() || genero == null || genero.isEmpty() || formato == null || formato.isEmpty()
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
        Publisher editorial = obtenerEditorialDesdeCombo(textoEditorial);
        if (editorial == null) {
            return new Response<>(Status.NOT_FOUND, "La editorial seleccionada no existe.");
        }
        double valor = convertirValor(valorTexto);
        List<Author> autores = validacionAutores.getDato();
        DigitalBook libro = new DigitalBook(titulo, autores, isbn, genero, formato, valor, editorial, enlace);
        registrarRelaciones(libro, autores, editorial, null);
        bookStorage.guardar(libro);
        return new Response<>(Status.CREATED, "Libro digital creado correctamente.", libro);
    }

    public Response<Book> crearAudiolibro(String titulo, List<String> autoresTexto, String isbn, String genero, String formato, String valorTexto, String textoEditorial, String duracionTexto, String idNarradorTexto) {
        Response<List<Author>> validacionAutores = validarAutores(autoresTexto);
        if (validacionAutores.getCodigo() != Status.OK) {
            return new Response<>(validacionAutores.getCodigo(), validacionAutores.getMensaje());
        }
        if (titulo == null || titulo.isEmpty() || genero == null || genero.isEmpty() || formato == null || formato.isEmpty()
                || valorTexto == null || valorTexto.isEmpty() || duracionTexto == null || duracionTexto.isEmpty() || idNarradorTexto == null || idNarradorTexto.isEmpty()) {
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
        Publisher editorial = obtenerEditorialDesdeCombo(textoEditorial);
        if (editorial == null) {
            return new Response<>(Status.NOT_FOUND, "La editorial seleccionada no existe.");
        }
        long idNarrador;
        try {
            idNarrador = Long.parseLong(idNarradorTexto);
        } catch (NumberFormatException e) {
            return new Response<>(Status.BAD_REQUEST, "El narrador debe tener un ID numérico.");
        }
        Narrator narrador = personStorage.buscarNarrador(idNarrador);
        if (narrador == null) {
            return new Response<>(Status.NOT_FOUND, "El narrador seleccionado no existe.");
        }
        double valor = convertirValor(valorTexto);
        List<Author> autores = validacionAutores.getDato();
        Audiobook libro = new Audiobook(titulo, autores, isbn, genero, formato, valor, editorial, duracion, narrador);
        registrarRelaciones(libro, autores, editorial, narrador);
        bookStorage.guardar(libro);
        return new Response<>(Status.CREATED, "Audiolibro creado correctamente.", libro);
    }

    private void registrarRelaciones(Book libro, List<Author> autores, Publisher editorial, Narrator narrador) {
        for (Author autor : autores) {
            List<Book> librosAutor = autor.getLibros();
            librosAutor.add(libro);
            autor.setLibros(librosAutor);
        }
        List<Book> librosEditorial = editorial.getLibros();
        librosEditorial.add(libro);
        editorial.setLibros(librosEditorial);
        if (narrador != null) {
            List<Book> librosNarrador = narrador.getAudiolibros();
            librosNarrador.add(libro);
            narrador.setAudiolibros(librosNarrador);
        }
    }

    public Response<List<Book>> obtenerLibros() {
        List<Book> copias = new ArrayList<>();
        for (Book libro : bookStorage.obtenerOrdenados()) {
            copias.add(libro.copiar());
        }
        return new Response<>(Status.OK, "Libros listados.", copias);
    }

    public Response<List<Book>> obtenerLibrosPorAutor(long idAutor) {
        Author autor = personStorage.buscarAutor(idAutor);
        if (autor == null) {
            return new Response<>(Status.NOT_FOUND, "El autor no existe.");
        }
        List<Book> copias = new ArrayList<>();
        for (Book libro : autor.getLibros()) {
            copias.add(libro.copiar());
        }

        // --- CAMBIO A ASCENDENTE por ISBN ---
        copias.sort((libro1, libro2) -> libro1.getIsbn().compareTo(libro2.getIsbn()));
        // --- FIN DE CAMBIO ---

        return new Response<>(Status.OK, "Libros del autor listados.", copias);
    }

    public Response<List<Book>> obtenerLibrosPorFormato(String formato) {
        List<Book> copias = new ArrayList<>();
        for (Book libro : bookStorage.obtenerOrdenados()) {
            if (libro.getFormato().equals(formato)) {
                copias.add(libro.copiar());
            }
        }

        // --- CAMBIO A ASCENDENTE por ISBN ---
        copias.sort((libro1, libro2) -> libro1.getIsbn().compareTo(libro2.getIsbn()));
        // --- FIN DE CAMBIO ---
        
        return new Response<>(Status.OK, "Libros filtrados por formato.", copias);
    }

    public Response<List<Author>> autoresConMasEditoriales() {
        List<Author> autores = personStorage.obtenerAutoresOrdenados();
        int maximo = -1;
        List<Author> resultado = new ArrayList<>();
        for (Author autor : autores) {
            Set<String> editoriales = new HashSet<>();
            for (Book libro : autor.getLibros()) {
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
        List<Author> copias = new ArrayList<>();
        for (Author autor : resultado) {
            copias.add(autor.copiar());
        }
        return new Response<>(Status.OK, "Autores con más editoriales listados.", copias);
    }
}
