package de.hochschulehannover.myprojects.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

import de.hochschulehannover.myprojects.R;

/**
 * <h2>Model Project</h2>
 * Modelklasse für ein Projekt. Die Klasse implementiert das {@link Parcelable} interface.
 * Damit kann das gesamte Objekt einfacher an Activities übergeben werden.
 *
 * <p>Ein Projekt besitzt folgende Attribute:</p>
 *  <ul>
 *   <li>Name: Name des Projekts</li>
 *   <li>Color: Farbe des Projekt, Standardfarbe: App Hauptfarbe</li>
 *   <li>userId: Name des Users der das Projekt erstellt hat (Name in Klartext und nicht die UID)</li>
 *   <li>assignedUsers: ArrayList bestehend aus den UIDs aller Nutzer, die zum Projekt hinzugefügt wurden</li>
 *   <li>Tag: Tag bzw. Kategorie des Projekts</li>
 *   <li>startDate: Datum an dem das Projekt beginnen soll</li>
 *   <li>endDate: Datum an dem das Projekt vorraussichtlich beendet werden soll</li>
 *   <li>documentId: Die documentId im Firestore</li>
 *   <li>status: Der Status des Projekts(Nicht angefangen, In Arbeit, Abgeschlossen, Abgebrochen)</li>
 *   <li>taskList: Eine ArrayList bestehend aus Objekten vom Typ TaskList. In dieser sind die Aufgabenlisten (Backlog, In Bearbeitung, Fertig) gespeichert</li>
 *  </ul>
 *
 * <b>Autor: Ole</b>
 * </p>
 */
public class Project implements Parcelable {
    public String name = "";
    public String color = String.valueOf(R.color.primary_app_color);
    public String userId = "";
    public ArrayList<String> assignedUsers = new ArrayList<>();
    public String tag = "";
    public String startDate = "";
    public String endDate = "";
    public String documentId = "";
    public String status = "";
    public ArrayList<TaskList> taskList = new ArrayList<>();

    public Project() {
        Log.i("Project", "Im Default-Konstruktor");
    }

    public Project(String name, String color, String userId, ArrayList<String> assignedUsers,
                   String tag, String startDate, String endDate, String status) {
        this.name = name;
        this.color = color;
        this.userId = userId;
        this.assignedUsers = assignedUsers;
        this.tag = tag;
        this.startDate = startDate;
        this.endDate = endDate;
        this.status = status;
    }

    protected Project(Parcel in) {
        name = in.readString();
        color = in.readString();
        userId = in.readString();
        assignedUsers = in.createStringArrayList();
        tag = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        documentId = in.readString();
        status = in.readString();
        taskList = in.createTypedArrayList(TaskList.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        dest.writeString(color);
        dest.writeString(userId);
        dest.writeStringList(assignedUsers);
        dest.writeString(tag);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeString(documentId);
        dest.writeString(status);
        dest.writeTypedList(taskList);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Project> CREATOR = new Creator<Project>() {
        @Override
        public Project createFromParcel(Parcel in) {
            return new Project(in);
        }

        @Override
        public Project[] newArray(int size) {
            return new Project[size];
        }
    };
}
