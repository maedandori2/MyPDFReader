package com.fujifilm.fb.docuworks.android.viewercomponent.view;

import java.util.Observable;

/* JADX INFO: loaded from: classes.dex */
public final class DrawerStatusObservable extends Observable {
    private static DrawerStatusObservable sObservable = new DrawerStatusObservable();

    private DrawerStatusObservable() {
    }

    public static DrawerStatusObservable getInstance() {
        return sObservable;
    }

    public static void notifyStatus(int i) {
        sObservable.updateDrawerStatus(i);
    }

    public synchronized void updateDrawerStatus(int i) {
        setChanged();
        notifyObservers(Integer.valueOf(i));
    }
}
