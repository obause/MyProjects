package de.hochschulehannover.myprojects.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.io.Serializable;

/**
 * <h2>Model User</h2>
 * Modelklasse für einen User. Die Klasse implementiert das {@link Parcelable} interface.
 * Damit kann das gesamte Objekt einfacher an Activities übergeben werden.
 *
 * <p>Ein User besitzt folgende Attribute:</p>
 * <ul>
 *  <li>id: Die von Firebase Authentication generierte UID</li>
 *  <li>name: Der Name des Nutzers</li>
 *  <li>email: Die Email-Adresse des Nutzers</li>
 *  <li>image: Die URL zum Profilbild des Nutzers</li>
 *  <li>fcmToken: Token für Benachrichtigungen (Funktion noch nicht implementiert)</li>
 * </ul>
 *
 * <b>Autor: Constantin</b>
 * </p>
 */
public final class User implements Parcelable {
    //TODO: Attribute privat setzen und getter/setter erstellen
    public String id = "";
    public String name = "";
    public String email = "";
    public String image = "";
    public String fcmToken = "";

    public User(String id, String  name, String email, String image, String fcmToken) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.image = image;
        this.fcmToken = fcmToken;
    }

    public User(String id, String  name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public User(String id, String  name, String email, String image) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.image = image;
    }

    public User() {
        Log.i("User", "Im Default-Konstruktor");
    }

    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        email = in.readString();
        image = in.readString();
        fcmToken = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(email);
        dest.writeString(image);
        dest.writeString(fcmToken);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };
}
