package de.hochschulehannover.myprojects.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

/**
 * <h2>Model Task</h2>
 * Modelklasse für eine Aufgabe. Die Klasse implementiert das {@link Parcelable} interface.
 * Damit kann das gesamte Objekt einfacher an Activities übergeben werden.
 *
 * <p>Ein Task besitzt folgende Attribute:</p>
 *  <ul>
 *   <li>name: Name/Bezeichnung der Aufgabe</li>
 *   <li>createdBy: Name des Nutzers der die Aufgabe erstellt hat</li>
 *   <li>assignedTo: ArrayList bestehend aus UIDs der Nutzer die der Aufgabe zugeordnet sind (wurde nicht mehr umgesetzt)</li>
 *   <li>status: Status der Aufgabe(Backlog, in Arbeit, Fertig)</li>
 *   <li>priority: Priorität der Aufgabe (Niedrig, Mittel, Hoch)</li>
 *   <li>description: Aufgabenbeschreibung</li>
 *  </ul>
 * <b>Autor(en):</b>
 * </p>
 */
public class Task implements Parcelable {
    public String name = "";
    public String createdBy = "";
    public ArrayList<String> assignedTo = new ArrayList<>();
    public String status = "";
    public String priotity = "";
    public String description = "";

    public Task() {
        Log.i("Task", "Im Default-Konstruktor");
    }

    public Task(String name, String createdBy, ArrayList<String> assignedTo, String status,
                String priotity, String description) {
        this.name = name;
        this.createdBy = createdBy;
        this.assignedTo = assignedTo;
        this.status = status;
        this.priotity = priotity;
        this.description = description;
    }

    protected Task(Parcel in) {
        name = in.readString();
        createdBy = in.readString();
        assignedTo = in.createStringArrayList();
        status = in.readString();
        priotity = in.readString();
        description = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(createdBy);
        dest.writeStringList(assignedTo);
        dest.writeString(status);
        dest.writeString(priotity);
        dest.writeString(description);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };
}
