package core.model.storage;

import core.model.interfaces.IPublisher;
import core.observer.Observer;
import core.observer.Subject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Repositorio para editoriales.
 * Patrón Singleton para garantizar una única instancia.
 */
public class PublisherStorage implements Subject {

    private static PublisherStorage instance = null;
    private final List<IPublisher> editoriales;
    private final List<Observer> observadores;

    private PublisherStorage() {
        this.editoriales = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    public static synchronized PublisherStorage getInstance() {
        if (instance == null) {
            instance = new PublisherStorage();
        }
        return instance;
    }

    public boolean existeNit(String nit) {
        for (IPublisher editorial : editoriales) {
            if (editorial.getNit().equals(nit)) {
                return true;
            }
        }
        return false;
    }

    public void guardar(IPublisher editorial) {
        this.editoriales.add(editorial);
        notifyObservers();
    }

    public IPublisher buscarPorNit(String nit) {
        for (IPublisher editorial : editoriales) {
            if (editorial.getNit().equals(nit)) {
                return editorial;
            }
        }
        return null;
    }

    public List<IPublisher> obtenerTodas() {
        return new ArrayList<>(editoriales);
    }

    public List<IPublisher> obtenerOrdenadasPorNit() {
        List<IPublisher> copia = new ArrayList<>(editoriales);
        Collections.sort(copia, Comparator.comparing(IPublisher::getNit));
        return copia;
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
