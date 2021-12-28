package de.hochschulehannover.myprojects.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Task implements Parcelable {
    public String name = "";
    public String createdBy = "";
    public ArrayList<String> assignedTo = new ArrayList<>();
    public String status = "";
    public String priotity = "";
    public String description = "";

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
