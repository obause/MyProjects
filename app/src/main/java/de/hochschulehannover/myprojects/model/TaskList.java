package de.hochschulehannover.myprojects.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * <h2>Model TaskList</h2>
 * Modelklasse für eine Aufgabenliste. Die Klasse implementiert das {@link Parcelable} interface.
 * Damit kann das gesamte Objekt einfacher an Activities übergeben werden.
 *
 * Eine TaskList repräsentiert eine Liste mit Aufgaben. Diese werden für die Tabs in der Aufgabenliste der
 * Projekte genutzt. Aufgabenlisten sind mit einem Projekt verknüpft.
 * Ursprünglich war geplant, dass ein Nutzer selber Aufgabenlisten für ein Projekt erstellen kann,
 * welche ihm dann als Tabs angezeigt werde. Dies wurde nicht mehr umgesetzt.
 * Daher hat jedes Projekt aktuell standardmäßig drei Aufgabenlisten, die sich nicht verändern: Backlog, In Arbeit und Fertig
 *
 * <p>Eine TaskList besitzt folgende Attribute:</p>
 * <ul>
 *  <li>name: Name der Liste</li>
 *  <li>createdBy: UID des Nutzers der die Liste erstellt hat</li>
 *  <li>tasks: ArrayList bestehend aus Objekten vom Typ Task. Diese beinhaltet alle Aufgaben, die dieser Aufgabenliste zugeordnet sind</li>
 * </ul>
 *
 * <b>Autor: Joshua</b>
 * </p>
 */
public class TaskList implements Parcelable {
    public String name = "";
    public String createdBy = "";
    public ArrayList<Task> tasks = new ArrayList<>();

    public TaskList(String name, String createdBy) {
        this.name = name;
        this.createdBy = createdBy;
    }

    public TaskList(String name, String createdBy, ArrayList<Task> tasks) {
        this.name = name;
        this.createdBy = createdBy;
        this.tasks = tasks;
    }

    public TaskList() {
        Log.i("Task", "Im Default-Konstruktor");
    }

    protected TaskList(Parcel in) {
        name = in.readString();
        createdBy = in.readString();
        tasks = in.createTypedArrayList(Task.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(createdBy);
        dest.writeTypedList(tasks);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<TaskList> CREATOR = new Creator<TaskList>() {
        @Override
        public TaskList createFromParcel(Parcel in) {
            return new TaskList(in);
        }

        @Override
        public TaskList[] newArray(int size) {
            return new TaskList[size];
        }
    };
}
