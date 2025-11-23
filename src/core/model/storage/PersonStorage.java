package core.model.storage;

import core.model.Author;
import core.model.Manager;
import core.model.Narrator;
import core.model.Person;
import core.observer.Observer;
import core.observer.Subject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Repositorio para personas.
 * Patrón Singleton para garantizar una única instancia.
 */
public class PersonStorage implements Subject {

    private static PersonStorage instance = null;
    private final List<Author> autores;
    private final List<Manager> gerentes;
    private final List<Narrator> narradores;
    private final List<Observer> observadores;

    private PersonStorage() {
        this.autores = new ArrayList<>();
        this.gerentes = new ArrayList<>();
        this.narradores = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    public static synchronized PersonStorage getInstance() {
        if (instance == null) {
            instance = new PersonStorage();
        }
        return instance;
    }

    public boolean existeId(long id) {
        for (Author autor : autores) {
            if (autor.getId() == id) {
                return true;
            }
        }
        for (Manager gerente : gerentes) {
            if (gerente.getId() == id) {
                return true;
            }
        }
        for (Narrator narrador : narradores) {
            if (narrador.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public void guardarAutor(Author autor) {
        this.autores.add(autor);
        notifyObservers();
    }

    public void guardarGerente(Manager gerente) {
        this.gerentes.add(gerente);
        notifyObservers();
    }

    public void guardarNarrador(Narrator narrador) {
        this.narradores.add(narrador);
        notifyObservers();
    }

    public Author buscarAutor(long id) {
        for (Author autor : autores) {
            if (autor.getId() == id) {
                return autor;
            }
        }
        return null;
    }

    public Manager buscarGerente(long id) {
        for (Manager gerente : gerentes) {
            if (gerente.getId() == id) {
                return gerente;
            }
        }
        return null;
    }

    public Narrator buscarNarrador(long id) {
        for (Narrator narrador : narradores) {
            if (narrador.getId() == id) {
                return narrador;
            }
        }
        return null;
    }

    public List<Author> obtenerAutoresOrdenados() {
        List<Author> copia = new ArrayList<>(autores);
        Collections.sort(copia, Comparator.comparingLong(Author::getId));
        return copia;
    }

    public List<Manager> obtenerGerentesOrdenados() {
        List<Manager> copia = new ArrayList<>(gerentes);
        Collections.sort(copia, Comparator.comparingLong(Manager::getId));
        return copia;
    }

    public List<Narrator> obtenerNarradoresOrdenados() {
        List<Narrator> copia = new ArrayList<>(narradores);
        Collections.sort(copia, Comparator.comparingLong(Narrator::getId));
        return copia;
    }

    public List<Person> obtenerTodasLasPersonasOrdenadas() {
        List<Person> todasLasPersonas = new ArrayList<>();
        todasLasPersonas.addAll(autores);
        todasLasPersonas.addAll(gerentes);
        todasLasPersonas.addAll(narradores);
        Collections.sort(todasLasPersonas, Comparator.comparingLong(Person::getId));
        return todasLasPersonas;
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
