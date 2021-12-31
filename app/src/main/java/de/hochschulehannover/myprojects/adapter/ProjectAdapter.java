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

import de.hochschulehannover.myprojects.R;
import de.hochschulehannover.myprojects.model.Project;

public class ProjectAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private LayoutInflater mInflater;
    private Context context;
    private ArrayList<Project> mData;
    private ItemClickListener mClickListener;

    public ProjectAdapter(Context context, ArrayList<Project> list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = list;

    }

    /**
     * Called when RecyclerView needs a new  ViewHolder of the given type to represent
     * an item.
     * <p>
     * This new ViewHolder should be constructed with a new View that can represent the items
     * of the given type. You can either create a new View manually or inflate it from an XML
     * layout file.
     * <p>
     * The new ViewHolder will be used to display items of the adapter using
     *  #onBindViewHolder(ViewHolder, int, List). Since it will be re-used to display
     * different items in the data set, it is a good idea to cache references to sub views of
     * the View to avoid unnecessary {@link View#findViewById(int)} calls.
     *
     * @param parent   The ViewGroup into which the new View will be added after it is bound to
     *                 an adapter position.
     * @param viewType The view type of the new View.
     * @return A new ViewHolder that holds a View of the given view type.
     * @see #getItemViewType(int)
     *  #onBindViewHolder(ViewHolder, int)
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


    /*
    Element aus Liste mit RecyclerView verbinden
     */
    /**
     * Called by RecyclerView to display the data at the specified position. This method should
     * update the contents of the { ViewHolder#itemView} to reflect the item at the given
     * position.
     * <p>
     * Note that unlike { ListView}, RecyclerView will not call this method
     * again if the position of the item changes in the data set unless the item itself is
     * invalidated or the new position cannot be determined. For this reason, you should only
     * use the <code>position</code> parameter while acquiring the related data item inside
     * this method and should not keep a copy of it. If you need the position of an item later
     * on (e.g. in a click listener), use { ViewHolder#getAdapterPosition()} which will
     * have the updated adapter position.
     * <p>
     * Override { #onBindViewHolder(ViewHolder, int, List)} instead if Adapter can
     * handle efficient partial bind.
     *
     * @param holder   The ViewHolder which should be updated to represent the contents of the
     *                 item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
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
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
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

    // parent activity will implement this method to respond to click events
    public interface ItemClickListener {
        void onClick(int position, Project model);
    }
}
