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
import de.hochschulehannover.myprojects.R;
import de.hochschulehannover.myprojects.model.Task;
import de.hochschulehannover.myprojects.model.User;

public class MemberListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private LayoutInflater mInflater;
    private Context context;
    private ArrayList<User> mData;
    private TaskListAdapter.ItemClickListener mClickListener;

    private CircleImageView userImage;

    public MemberListAdapter(Context context, ArrayList<User> list) {
        this.mInflater = LayoutInflater.from(context);
        this.context = context;
        this.mData = list;
    }

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

    @Override
    public int getItemCount() {
        return mData.size();
    }

    // Click-Events abfangen und neues Objekt vom Interface übergeben
    public void setOnClickListener(TaskListAdapter.ItemClickListener itemClickListener) {
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
