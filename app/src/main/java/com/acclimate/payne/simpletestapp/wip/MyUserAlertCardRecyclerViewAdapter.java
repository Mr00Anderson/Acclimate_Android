package com.acclimate.payne.simpletestapp.wip;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.acclimate.payne.simpletestapp.R;
import com.acclimate.payne.simpletestapp.alerts.UserAlert;
import com.acclimate.payne.simpletestapp.wip.UserAlertCardFragment.OnListFragmentInteractionListener;

import java.util.List;


public class MyUserAlertCardRecyclerViewAdapter
        extends RecyclerView.Adapter<MyUserAlertCardRecyclerViewAdapter.ViewHolder> {

    private final List<UserAlert> mValues;
    private final OnListFragmentInteractionListener mListener;

    public MyUserAlertCardRecyclerViewAdapter(List<UserAlert> items, OnListFragmentInteractionListener listener) {
        mValues = items;
        mListener = listener;
    }

    @Override @NonNull
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.useralertcard_fragment, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        UserAlert alert = holder.mItem = mValues.get(position);
        // holder.alertId.setText(alert.getId());
        holder.description.setText(alert.getDateDeMiseAJour());

        holder.mView.setOnClickListener( view -> {
                if (null != mListener) {
                    // Notify the active callbacks interface (the activity, if the
                    // fragment is attached to one) that an item has been selected.
                    mListener.onListFragmentInteraction(holder.mItem);
                }

        });
    }

    @Override
    public int getItemCount() {
        return mValues.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public UserAlert mItem;
        public final View mView;
        public final TextView userBubbleScore;
        public final TextView title;
        public final TextView subCat;
        public final TextView description;
        public final TextView certitude;
        // public final TextView date;
        // public final TextView severity;
        // public final TextView alertId;
        // public final TextView pos;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            title = mView.findViewById(R.id.user_bubble_title);
            subCat = mView.findViewById(R.id.user_bubble_subcat);
            description = mView.findViewById(R.id.user_bubble_description);
            certitude = mView.findViewById(R.id.user_bubble_certitude);
            userBubbleScore = mView.findViewById(R.id.user_bubble_alert_current_score);
        }

        @Override
        public String toString() {
            return super.toString() + " '" + description.getText() + "'";
        }
    }
}
