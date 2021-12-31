package de.hochschulehannover.myprojects.adapter;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;

import java.util.ArrayList;

import de.hochschulehannover.myprojects.R;
import de.hochschulehannover.myprojects.model.Task;

public class TaskListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private Context context;
    private ArrayList<Task> mData;
    private ItemClickListener mClickListener;

    public TaskListAdapter(Context context, ArrayList<Task> list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = mInflater.inflate(
                R.layout.fragment_task_list_item,
                parent,
                false
        );
        //LinearLayout layoutParams = LinearLayout.LayoutParams((parent.getWidth().toI))
        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Task model = mData.get(position);

        ShapeableImageView doneIcon = holder.itemView.findViewById(R.id.done_icon);
        TextView taskTitleTextView = holder.itemView.findViewById(R.id.taskTitleTextView);
        TextView taskStatusTextView = holder.itemView.findViewById(R.id.taskStatusTextView);
        TextView taskByTextView = holder.itemView.findViewById(R.id.taskByTextView);
        ShapeableImageView editIcon = holder.itemView.findViewById(R.id.edit_icon);

        taskTitleTextView.setText(model.name);
        taskStatusTextView.setText(model.status);
        taskByTextView.setText(model.createdBy);

        /*if (model.status != "Abgeschlossen") {
            doneIcon.setColorFilter(Color.argb(100,61,87,117));
        }*/
        Log.i("StatusIcon", "Status ist:" + model.status);
        if (model.status == "Backlog") {
            Log.i("StatusIcon", "Icon für Backlog anpassen");
            doneIcon.setImageResource(R.drawable.ic_baseline_playlist_play_24);
        } else if (model.status == "In Arbeit") {
            Log.i("StatusIcon", "Icon für In Arbeit anpassen");
            doneIcon.setImageResource(R.drawable.ic_baseline_check_24);
        }

        doneIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onClick(position, model, view, "changeStatus");
                }
            }
        });

        editIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onClick(position, model, view, "edit");
                }
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onClick(position, model, view, "edit");
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // Click-Events abfangen und neues Objekt vom Interface übergeben
    public void setOnClickListener(ItemClickListener itemClickListener) {
        this.mClickListener = itemClickListener;
    }

    //Innere Klasse
    private class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onClick(int position, Task task, View view, String s);
    }
}
