package com.za.androidauthenticator.adapters;

import android.content.Context;
import android.database.Observable;
import android.os.SystemClock;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.za.androidauthenticator.R;
import com.za.androidauthenticator.data.contract.SiteIconContract;
import com.za.androidauthenticator.data.entity.AuthCode;

import java.util.ArrayList;
import java.util.List;

/**
 * RecycleView Adapter for list News
 */
public class AuthCodeAdapter extends RecyclerView.Adapter<AuthCodeAdapter.ViewHolder> {

    public static final String PAYLOAD_TIME = "PAYLOAD_TIME";
    public static final String PAYLOAD_CODE = "PAYLOAD_CODE";

    private final List<AuthCode> listCodes = new ArrayList<>();

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        View contactView = inflater.inflate(R.layout.content_authcodes_row, parent, false);
        return new ViewHolder(contactView);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position, @NonNull List<Object> payloads) {
        if (!payloads.isEmpty()) {
            AuthCode item = listCodes.get(position);
            for (Object payload : payloads) {
                if (PAYLOAD_TIME.equals(payload)) {
                    // in this case only time will be updated
                    holder.time.setText(Integer.toString(item.reTime));
                } else if (PAYLOAD_CODE.equals(payload)) {
                    // only code will be updated
                    holder.code.setText(item.currentCode);
                }
            }
        } else {
            // in this case regular onBindViewHolder will be called
            super.onBindViewHolder(holder, position, payloads);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        AuthCode item = listCodes.get(position);

        // Set text
        holder.siteName.setText(item.siteName);
        holder.accountName.setText(item.accountName);

        // Set site icon
        holder.siteIcon.setImageResource(SiteIconContract.getIconId(item.siteName));

        // animation
        Animation ani = AnimationUtils.loadAnimation(holder.itemView.getContext(),
                R.anim.item_animation_scale);
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
           return oldList.get(oldItemPosition).codeId == newList.get(newItemPosition).codeId;
        }

        @Override
        public boolean areContentsTheSame(int oldItemPosition, int newItemPosition) {
            return oldList.get(oldItemPosition).equals(newList.get(newItemPosition));
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
        TextView time;
        TextView code;

        ViewHolder(final View itemView) {
            super(itemView);
            siteName = itemView.findViewById(R.id.siteNameLayout);
            accountName = itemView.findViewById(R.id.accountName);
            siteIcon = itemView.findViewById(R.id.siteIcon);
            container = itemView.findViewById(R.id.container);
            time = itemView.findViewById(R.id.time);
            code = itemView.findViewById(R.id.code);

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