package core.observer;

/**
 * Sujeto observado por las vistas.
 */
public interface Subject {
    void registerObserver(Observer observer);
    void notifyObservers();
}
