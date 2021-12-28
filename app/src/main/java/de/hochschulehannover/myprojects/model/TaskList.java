package de.hochschulehannover.myprojects.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;
import java.util.Arrays;

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
