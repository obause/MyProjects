package de.hochschulehannover.myprojects.model;

import android.graphics.Color;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

import de.hochschulehannover.myprojects.R;

public class Project implements Parcelable {
    public String name = "";
    public Integer color = R.color.primary_app_color;
    public String userId = "";
    public ArrayList<String> assignedUsers = new ArrayList<>();
    public String tag = "";
    public String startDate = "";
    public String endDate = "";
    public String documentId = "";
    public String status = "";
    public ArrayList<Task> taskList = new ArrayList<>();

    public Project() {
        Log.i("Project", "Im Default-Konstruktor");
    }

    public Project(String name, Integer color, String userId, ArrayList<String> assignedUsers,
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
        if (in.readByte() == 0) {
            color = null;
        } else {
            color = in.readInt();
        }
        userId = in.readString();
        assignedUsers = in.createStringArrayList();
        tag = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        documentId = in.readString();
        status = in.readString();
        taskList = in.createTypedArrayList(Task.CREATOR);
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(name);
        if (color == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(color);
        }
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
