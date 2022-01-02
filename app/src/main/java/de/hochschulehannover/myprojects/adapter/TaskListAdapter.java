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
import de.hochschulehannover.myprojects.model.Project;
import de.hochschulehannover.myprojects.model.Task;

/**
 * <h2>Adapterklasse TaskListAdapter</h2>
 *
 * Adapterklasse, die den entsprechenden RecyclerView des Fragments mithilfe eines ViewHolders
 * mit den Aufgaben befüllt.
 * Diese erbt von {@link RecyclerView.Adapter}.
 *
 * Es werden alle Aufgaben, die dem entsprechenden Status(Tab) zugeordnet sind für das UI
 * erstellt.
 *
 *<p>
 * <b>Autor(en):</b>
 * </p>
 */
public class TaskListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private Context context;
    private ArrayList<Task> mData;
    private ItemClickListener mClickListener;

    /**
     * Konstruktor zum initialisieren des Objekts.
     * @param context Der Context des aufrufenden Fragments.
     * @param list Eine ArrayList bestehend aus den Aufgaben des Status
     *             (Jede Aufgabe ist ein Objekt von {@link Task}) -> Eine {@link de.hochschulehannover.myprojects.model.TaskList}
     */
    public TaskListAdapter(Context context, ArrayList<Task> list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = list;
    }

    /**
     * Wird aufgerufen, wenn der RecyclerView einen neuen ViewHolder benötigt, um ein Element
     * (Aufgabe/Task) zu repräsentieren.
     *
     * Der ViewHolder wird mit einem neuen View initialisiert, das die Elemente(Aufgaben)
     * repräsentiert. Die View wird von der angegebenen XML fragment_task_list_item.xml inflated.
     *
     * Das ViewHolder Objekt wird dann dazu genutzt die Elemente(Aufgaben) des Adapers mithilfe von
     * #onBindViewHolder(ViewHolder, int, List) anzuzeigen.
     *
     * @param parent   Die ViewGroup in die die neue View hinzugefügt wird nachdem es zum Adapter
     *                 hinzugefügt wurde.
     * @param viewType Der View Type der neuen View.
     * @return Eine neues ViewHolder-Objekt, das die entsprechende View beinhaltet.
     */
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

    /**
     * Wird vom RecyclerView aufgerufen, um die Daten anzuzeigen. Diese Methode aktualisiert die
     * Elemente des ViewHolders mit den Attributen des Tasks.
     * -> Element aus der Aufgabenliste(TaskList) wird mit Recyclerview verbunden.
     *
     * @param holder   Das ViewHolder-Objekt, in dem die Elemente mit den Attributen der Aufgaben
     *                 aktualisiert werden.
     * @param position Die Position des Elements im Adapter.
     */
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

    /**
     * Gibt die Gesamtanzahl der Elemente in den Daten(ArrayList der  Projekte) zurück, die sich im
     * Adapter befinden
     *
     * @return Anzahl Elemente im Adapter.
     */
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

    // Übergeprdnete Activity implementiert diese Methode, um eine Ereignisbehandlung der Klicks auf
    // Elemente zu ermöglichen.
    public interface ItemClickListener {
        void onClick(int position, Task task, View view, String s);
    }
}
