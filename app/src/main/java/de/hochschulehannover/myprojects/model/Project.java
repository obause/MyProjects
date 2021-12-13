package de.hochschulehannover.myprojects.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

public class Project implements Parcelable {
    public String name = "";
    public String color = "";
    public String userId = "";
    public ArrayList<String> assignedUsers = new ArrayList<>();
    public String tag = "";
    public String startDate = "";
    public String endDate = "";

    public Project(String name, String color, String userId, ArrayList<String> assignedUsers,
                   String tag, String startDate, String endDate) {
        this.name = name;
        this.color = color;
        this.userId = userId;
        this.assignedUsers = assignedUsers;
        this.tag = tag;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Project(Parcel in) {
        name = in.readString();
        color = in.readString();
        userId = in.readString();
        assignedUsers = in.createStringArrayList();
        tag = in.readString();
        startDate = in.readString();
        endDate = in.readString();
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(name);
        parcel.writeString(color);
        parcel.writeString(userId);
        parcel.writeStringList(assignedUsers);
        parcel.writeString(tag);
        parcel.writeString(startDate);
        parcel.writeString(endDate);
    }
}
