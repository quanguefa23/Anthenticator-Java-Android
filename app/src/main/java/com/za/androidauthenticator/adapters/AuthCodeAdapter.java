package com.za.androidauthenticator.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.data.contract.SiteIconContract;
import com.za.androidauthenticator.data.model.AuthCode;

import java.util.List;

/**
 * RecycleView Adapter for list News
 */
public class AuthCodeAdapter extends RecyclerView.Adapter<AuthCodeAdapter.ViewHolder> {

    private int layout;
    private List<AuthCode> listCodes;
    private Context context;

    public AuthCodeAdapter(Context context, int layout, List<AuthCode> listCodes) {
        this.context = context;
        this.layout = layout;
        this.listCodes = listCodes;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);

        View contactView = inflater.inflate(layout, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AuthCode authCode = listCodes.get(position);

        // Set text
        holder.siteName.setText(authCode.getSiteName());
        holder.accountName.setText(authCode.getAccountName());

        // Set site icon
        holder.siteIcon.setImageResource(SiteIconContract.getIconId(authCode.getSiteName()));

        Animation ani = AnimationUtils.loadAnimation(context, R.anim.item_animation_scale);
        holder.container.startAnimation(ani);
    }

    @Override
    public int getItemCount() {
        return listCodes.size();
    }

    public void updateDataAndNotify(List<AuthCode> newListCodes) {
        final NewsDiffCallback diffCallback = new NewsDiffCallback(listCodes, newListCodes);
        final DiffUtil.DiffResult diffResult = DiffUtil.calculateDiff(diffCallback);

        listCodes.clear();

        if (newListCodes != null)
            listCodes.addAll(newListCodes);
        diffResult.dispatchUpdatesTo(this);
    }

    public List<AuthCode> getListCodes() {
        return listCodes;
    }

    public static class NewsDiffCallback extends DiffUtil.Callback {
        private final List<AuthCode> oldList;
        private final List<AuthCode> newList;

        public NewsDiffCallback(List<AuthCode> oldData, List<AuthCode> newData) {
            this.oldList = oldData;
            this.newList = newData;
        }

        @Override
        public int getOldListSize() {
            return (oldList != null) ? oldList.size() : 0;
        }

        @Override
        public int getNewListSize() {
            return (newList != null) ? newList.size() : 0;
        }

        @Override
        public boolean areItemsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return true;
        }
    }

    // Define listener member variable
    private OnItemClickListener listener;

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView siteName;
        TextView accountName;
        ImageView siteIcon;
        View container;

        ViewHolder(final View itemView) {
            super(itemView);
            siteName = itemView.findViewById(R.id.siteNameLayout);
            accountName = itemView.findViewById(R.id.accountName);
            siteIcon = itemView.findViewById(R.id.siteIcon);
            container = itemView.findViewById(R.id.container);

            // Setup the click listener
            itemView.setOnClickListener(v -> {
                // Triggers click upwards to the adapter on click
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }
}