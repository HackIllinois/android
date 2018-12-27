package org.hackillinois.android.view.schedule;

import android.content.Context;
import android.content.Intent;
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
import org.hackillinois.android.view.EventInfoActivity;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.MyViewHolder> {
    private List<Event> mEventList;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView tv_eventName;
        TextView tv_eventDescription;
        TextView tv_eventLocation;
        ImageButton imageButton_star;
        ConstraintLayout constraintLayout_recyclerView;
        ConstraintLayout constraintLayout_event_onclick;

        public MyViewHolder(View parent) {
            super(parent);
            tv_eventName = itemView.findViewById(R.id.eventTitle);
            tv_eventDescription = itemView.findViewById(R.id.eventDetail);
            tv_eventLocation = itemView.findViewById(R.id.eventLocation);
            imageButton_star = itemView.findViewById(R.id.star);
            constraintLayout_recyclerView = itemView.findViewById(R.id.constraintLayout);
            constraintLayout_event_onclick = itemView.findViewById(R.id.constraintLayout_event_onclick);
        }
    }

    public EventsAdapter(List<Event> eventsList) {
        mEventList = eventsList;
    }

    @Override
    public EventsAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_event_list_item, parent, false);
        EventsAdapter.MyViewHolder viewHolder = new EventsAdapter.MyViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final Event event = mEventList.get(position);

        holder.constraintLayout_event_onclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, EventInfoActivity.class);
                intent.putExtra("event", event);
                context.startActivity(intent);
            }
        });

        holder.tv_eventName.setText(event.getName());
        holder.tv_eventDescription.setText(event.getDescription());
        holder.tv_eventLocation.setText(event.getLocationDescription());
        holder.imageButton_star.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                button.setSelected(!button.isSelected());

                if (button.isSelected()) {
                    // TODO: come up with a string
                    Snackbar.make(button, R.string.snackbar_notifications_on_text,
                            Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    // TODO: Handle de-select state change
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return mEventList.size();
    }
}