package org.hackillinois.android.view.schedule;

import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hackillinois.android.R;
import org.hackillinois.android.model.Event;
import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> {
    private List<Event> mEventList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder

    public class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        TextView tv_eventTitle;
        TextView tv_eventDetail;
        TextView tv_eventLocation;
        ImageButton imageButton_star;
        ConstraintLayout constraintLayout_recyclerView;

        public MyViewHolder(View parent) {
            super(parent);
            tv_eventTitle = itemView.findViewById(R.id.eventTitle);
            tv_eventDetail = itemView.findViewById(R.id.eventDetail);
            tv_eventLocation = itemView.findViewById(R.id.eventLocation);
            imageButton_star = itemView.findViewById(R.id.star);
            constraintLayout_recyclerView = itemView.findViewById(R.id.constraintLayout);
        }
    }

    public EventsAdapter(List<Event> eventsList) {
        mEventList = eventsList;
    }

    @Override
    public EventsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_events, parent, false);
        EventsAdapter.MyViewHolder viewHolder = new EventsAdapter.MyViewHolder(view);
        return viewHolder;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        Event event = mEventList.get(position);

        holder.tv_eventTitle.setText(event.getName());
        holder.tv_eventDetail.setText(event.getDescription());
        holder.tv_eventLocation.setText(event.getLocationDescription());
        holder.imageButton_star.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                button.setSelected(!button.isSelected());

                if (button.isSelected()) {
                    // TODO: pop up a snackbar? when they click the notification star
                    // make sure they don't stack up when they go through and get a lot of notifications?
                    // also make sure you can just use this instead of findviewbyid
                    // TODO: come up with a string
                    Snackbar.make(button, R.string.snackbar_notifications_on_text,
                            Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    //TODO: Handle de-select state change
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }
}