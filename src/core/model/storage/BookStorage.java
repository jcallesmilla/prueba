package core.model.storage;

import core.model.Book;
import core.observer.Observer;
import core.observer.Subject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Repositorio para libros de cualquier formato.
 * Patrón Singleton para garantizar una única instancia.
 */
public class BookStorage implements Subject {

    private static BookStorage instance = null;
    private final List<Book> libros;
    private final List<Observer> observadores;

    private BookStorage() {
        this.libros = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    public static synchronized BookStorage getInstance() {
        if (instance == null) {
            instance = new BookStorage();
        }
        return instance;
    }

    public boolean existeIsbn(String isbn) {
        for (Book libro : libros) {
            if (libro.getIsbn().equals(isbn)) {
                return true;
            }
        }
        return false;
    }

    public void guardar(Book libro) {
        this.libros.add(libro);
        notifyObservers();
    }

    public List<Book> obtenerOrdenados() {
        List<Book> copia = new ArrayList<>(libros);
        Collections.sort(copia, Comparator.comparing(Book::getIsbn));
        return copia;
    }

    public Book buscarPorIsbn(String isbn) {
        for (Book libro : libros) {
            if (libro.getIsbn().equals(isbn)) {
                return libro;
            }
        }
        return null;
    }

    @Override
    public void registerObserver(Observer observer) {
        if (!observadores.contains(observer)) {
            observadores.add(observer);
        }
    }

    @Override
    public void notifyObservers() {
        for (Observer observer : observadores) {
            observer.update();
        }
    }
}
