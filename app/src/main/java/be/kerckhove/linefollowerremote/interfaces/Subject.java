package be.kerckhove.linefollowerremote.interfaces;

/**
 * Created by Stijn on 12/12/2017.
 */

public interface Subject {
    public void register(Observer observer);
    public void unregister(Observer observer);
    public void notifyObservers();
}
