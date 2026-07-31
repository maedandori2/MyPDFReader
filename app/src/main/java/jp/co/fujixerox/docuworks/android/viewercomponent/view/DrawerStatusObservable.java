package jp.co.fujixerox.docuworks.android.viewercomponent.view;
import java.util.Observable;
public class DrawerStatusObservable extends Observable {
    private static DrawerStatusObservable sObservable = new DrawerStatusObservable();
    private DrawerStatusObservable() {}
    public static DrawerStatusObservable getInstance() { return sObservable; }
    public synchronized void updateDrawerStatus(int i) {
        setChanged();
        notifyObservers(Integer.valueOf(i));
    }
    public static void notifyStatus(int i) {
        sObservable.updateDrawerStatus(i);
    }
}
