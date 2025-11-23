package core.model.storage;

import core.model.Stand;
import core.observer.Observer;
import core.observer.Subject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;


public class StandStorage implements Subject {

    private final List<Stand> stands;
    private final List<Observer> observadores;

    public StandStorage() {
        this.stands = new ArrayList<>();
        this.observadores = new ArrayList<>();
    }

    public boolean existeId(long id) {
        for (Stand stand : stands) {
            if (stand.getId() == id) {
                return true;
            }
        }
        return false;
    }

    public void guardar(Stand stand) {
        this.stands.add(stand);
        notifyObservers();
    }

    public Stand buscarPorId(long id) {
        for (Stand stand : stands) {
            if (stand.getId() == id) {
                return stand;
            }
        }
        return null;
    }

    public List<Stand> obtenerOrdenados() {
        List<Stand> copia = new ArrayList<>(stands);
        Collections.sort(copia, Comparator.comparingLong(Stand::getId));
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
