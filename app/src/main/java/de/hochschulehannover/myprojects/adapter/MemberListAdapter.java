package de.hochschulehannover.myprojects.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import de.hochschulehannover.myprojects.BaseActivity;
import de.hochschulehannover.myprojects.R;
import de.hochschulehannover.myprojects.TaskListActivity;
import de.hochschulehannover.myprojects.model.Project;
import de.hochschulehannover.myprojects.model.Task;
import de.hochschulehannover.myprojects.model.User;

/**
 * <h2>Adapterklasse MemberListAdapter</h2>
 *
 * Adapterklasse, die den RecyclerView der Activity {@link de.hochschulehannover.myprojects.ProjectMembersActivity}
 * mithilfe eines ViewHolders mit den zugeordneten Nutzern des Projekts befüllt.
 *
 * Diese erbt von {@link RecyclerView.Adapter}.
 *
 * Es werden alle Nutzer die dem Projekt zugeordnet sind für das UI erstellt.
 *
 *<p>
 * <b>Autor: Ole</b>
 * </p>
 */
public class MemberListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private LayoutInflater mInflater;
    private Context context;
    private ArrayList<User> mData;
    private TaskListAdapter.ItemClickListener mClickListener;

    private CircleImageView userImage;

    /**
     * Konstruktor zum initialisieren des Objekts.
     * @param context Der Context der aufrufenden Activity
     * @param list Eine ArrayList bestehend aus den Usern (Jedes Element ist ein Objekt von {@link User})
     */
    public MemberListAdapter(Context context, ArrayList<User> list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = list;
    }

    /**
     * Wird aufgerufen, wenn der RecyclerView einen neuen ViewHolder benötigt, um ein Element
     * (Nutzer) zu repräsentieren.
     *
     * Der ViewHolder wird mit einem neuen View initialisiert, das die Elemente(Nutzer)
     * repräsentiert. Die View wird von der angegebenen XML item_list_members.xml inflated.
     *
     * Das ViewHolder Objekt wird dann dazu genutzt die Elemente(Nutzer) des Adapers mithilfe von
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
                R.layout.item_list_members,
                parent,
                false
        );
        //LinearLayout layoutParams = LinearLayout.LayoutParams((parent.getWidth().toI))
        return new MyViewHolder(view);
    }

    /**
     * Wird vom RecyclerView aufgerufen, um die Daten anzuzeigen. Diese Methode aktualisiert die
     * Elemente des ViewHolders mit den Attributen eines Nutzers.
     * -> Element aus der Userliste wird mit Recyclerview verbunden.
     *
     * @param holder   Das ViewHolder-Objekt, in dem die Elemente mit den Nutzerdaten aktualisiert werden.
     * @param position Die Position des Elements im Adapter.
     */
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        User model = mData.get(position);

        userImage = holder.itemView.findViewById(R.id.memberImageView);
        TextView memberNameTextView = holder.itemView.findViewById(R.id.memberNameTextView);
        TextView memberMailTextView = holder.itemView.findViewById(R.id.memberMailTextView);

        Glide
                .with(context)              // Kontext der Activity
                .load(model.image)          // URL des Bildes
                .centerCrop()               // Automatisch zuschneiden und zentrieren
                .placeholder(R.drawable.ic_user_place_holder) // Platzhalterbild
                .into(userImage);

        memberNameTextView.setText(model.name);
        memberMailTextView.setText(model.email);
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
    public void setOnClickListener(TaskListAdapter.ItemClickListener itemClickListener) {
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
        void onClick(int position, Task task, View view, String s);
    }
}
