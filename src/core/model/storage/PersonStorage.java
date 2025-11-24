package core.model.storage;

import core.model.interfaces.IAuthor;
import core.model.interfaces.IManager;
import core.model.interfaces.INarrator;
import core.model.interfaces.IPerson;
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
    private final List<IAuthor> autores;
    private final List<IManager> gerentes;
    private final List<INarrator> narradores;
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
        for (IAuthor autor : autores) {
            if (autor.getId() == id) {
                return true;
            }
        }
        for (IManager gerente : gerentes) {
            if (gerente.getId() == id) {
                return true;
            }
        }
        for (INarrator narrador : narradores) {
            if (narrador.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public void guardarAutor(IAuthor autor) {
        this.autores.add(autor);
        notifyObservers();
    }

    public void guardarGerente(IManager gerente) {
        this.gerentes.add(gerente);
        notifyObservers();
    }

    public void guardarNarrador(INarrator narrador) {
        this.narradores.add(narrador);
        notifyObservers();
    }

    public IAuthor buscarAutor(long id) {
        for (IAuthor autor : autores) {
            if (autor.getId() == id) {
                return autor;
            }
        }
        return null;
    }

    public IManager buscarGerente(long id) {
        for (IManager gerente : gerentes) {
            if (gerente.getId() == id) {
                return gerente;
            }
        }
        return null;
    }

    public INarrator buscarNarrador(long id) {
        for (INarrator narrador : narradores) {
            if (narrador.getId() == id) {
                return narrador;
            }
        }
        return null;
    }

    public List<IAuthor> obtenerAutoresOrdenados() {
        List<IAuthor> copia = new ArrayList<>(autores);
        Collections.sort(copia, Comparator.comparingLong(IAuthor::getId));
        return copia;
    }

    public List<IManager> obtenerGerentesOrdenados() {
        List<IManager> copia = new ArrayList<>(gerentes);
        Collections.sort(copia, Comparator.comparingLong(IManager::getId));
        return copia;
    }

    public List<INarrator> obtenerNarradoresOrdenados() {
        List<INarrator> copia = new ArrayList<>(narradores);
        Collections.sort(copia, Comparator.comparingLong(INarrator::getId));
        return copia;
    }

    public List<IPerson> obtenerTodasLasPersonasOrdenadas() {
        List<IPerson> todasLasPersonas = new ArrayList<>();
        todasLasPersonas.addAll(autores);
        todasLasPersonas.addAll(gerentes);
        todasLasPersonas.addAll(narradores);
        Collections.sort(todasLasPersonas, Comparator.comparingLong(IPerson::getId));
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
