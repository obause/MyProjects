package de.hochschulehannover.myprojects.adapter;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import de.hochschulehannover.myprojects.BaseActivity;
import de.hochschulehannover.myprojects.R;
import de.hochschulehannover.myprojects.TaskListActivity;
import de.hochschulehannover.myprojects.model.Project;

/**
 * <h2>Adapterklasse ProjectAdapter</h2>
 *
 * Adapterklasse, die den entsprechenden RecyclerView mithilfe eines ViewHolders mit den Projekten befüllt.
 * Diese erbt von {@link RecyclerView.Adapter}.
 *
 * Es werden alle Projekte die dem eingeloggten Nutzer zugeordnet sind für das UI erstellt.
 *
 *<p>
 * <b>Autor(en):</b>
 * </p>
 */
public class ProjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private Context context;
    private ArrayList<Project> mData;
    private ItemClickListener mClickListener;

    /**
     * Konstruktor zum initialisieren des Objekts.
     * @param context Der Context der aufrufenden Activity
     * @param list Eine ArrayList bestehend aus den Projekten (Jedes Projekt ist ein Objekt von {@link Project})
     */
    public ProjectAdapter(Context context, ArrayList<Project> list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = list;
    }

    /**
     * Wird aufgerufen, wenn der RecyclerView einen neuen ViewHolder benötigt, um ein Element
     * (Projekt) zu repräsentieren.
     *
     * Der ViewHolder wird mit einem neuen View initialisiert, das die Elemente(Projekte)
     * repräsentiert. Die View wird von der angegebenen XML content_project_card.xml inflated.
     *
     * Das ViewHolder Objekt wird dann dazu genutzt die Elemente(Projekte) des Adapers mithilfe von
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
                R.layout.content_project_card,
                parent,
                false
        );
        return new MyViewHolder(view);
    }

    /**
     * Wird vom RecyclerView aufgerufen, um die Daten anzuzeigen. Diese Methode aktualisiert die
     * Elemente des ViewHolders mit den Attributen eines Projekts.
     * -> Element aus der Projektliste wird mit Recyclerview verbunden.
     *
     * TODO: anstatt den Parameter position zu nehmen, holder.getAdapterPosition() nutzen
     * Grund: Wenn sich die Position des Elements in der Liste ändert, ändert sich die Position nicht.
     * Wenn es später möglich sein soll die Anordnung der Liste änzupassen muss das geändert werden
     *
     * @param holder   Das ViewHolder-Objekt, in dem die Elemente mit den Projektdaten aktualisiert werden.
     * @param position Die Position des Elements im Adapter.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Project model = mData.get(position);

        ImageView projectColorImageView = holder.itemView.findViewById(R.id.projectColorImageView);
        TextView projectNameTextView = holder.itemView.findViewById(R.id.projectNameTextView);
        TextView projectTagTextView = holder.itemView.findViewById(R.id.projectTagTextView);
        TextView projectStatusTextView = holder.itemView.findViewById(R.id.projectStatusTextView);
        TextView projectOwnerTextView = holder.itemView.findViewById(R.id.projectOwnerTextView);

        projectColorImageView.setBackgroundTintList(ColorStateList.valueOf(Integer.valueOf(model.color)));
        projectNameTextView.setText(model.name);
        projectTagTextView.setText(model.tag);
        projectStatusTextView.setText(model.status);
        projectOwnerTextView.setText("Erstellt von: " + model.userId);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mClickListener != null) {
                    mClickListener.onClick(position, model);
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

    //Innere Klasse MyViewHolder
    private class MyViewHolder extends RecyclerView.ViewHolder {

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    // Übergeprdnete Activity implementiert diese Methode, um eine Ereignisbehandlung der Klicks auf
    // Elemente zu ermöglichen.
    public interface ItemClickListener {
        void onClick(int position, Project model);
    }
}
