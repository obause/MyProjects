package de.hochschulehannover.myprojects.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Task implements Parcelable {
    public String name = "";
    public String createdBy = "";
    public String priority = "";
    public String status = "";

    public Task(String name, String createdBy, String priority, String status) {
        this.name = name;
        this.createdBy = createdBy;
        this.priority = priority;
        this.status = status;
    }

    public Task() {
        Log.i("Task", "Im Default-Konstruktor");
    }

    protected Task(Parcel in) {
        name = in.readString();
        createdBy = in.readString();
        priority = in.readString();
        status = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(createdBy);
        parcel.writeString(priority);
        parcel.writeString(status);
    }
}
