package core.model.storage;

import core.model.Publisher;
import core.observer.Observer;
import core.observer.Subject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class PublisherStorage implements Subject {

    private final List<Publisher> editoriales;
    private final List<Observer> observadores;

    public PublisherStorage() {
        this.editoriales = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    public boolean existeNit(String nit) {
        for (Publisher editorial : editoriales) {
            if (editorial.getNit().equals(nit)) {
                return true;
            }
        }
        return false;
    }

    public void guardar(Publisher editorial) {
        this.editoriales.add(editorial);
        notifyObservers();
    }

    public Publisher buscarPorNit(String nit) {
        for (Publisher editorial : editoriales) {
            if (editorial.getNit().equals(nit)) {
                return editorial;
            }
        }
        return null;
    }

    public List<Publisher> obtenerOrdenados() {
        List<Publisher> copia = new ArrayList<>(editoriales);
        Collections.sort(copia, Comparator.comparing(Publisher::getNit));
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
