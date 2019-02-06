package org.hackillinois.android.view.schedule;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import org.hackillinois.android.R;
import org.hackillinois.android.database.entity.Event;
import org.hackillinois.android.view.EventInfoActivity;
import org.hackillinois.android.common.FavoritesManager;

import java.util.List;

public class EventsAdapter extends RecyclerView.Adapter<EventsAdapter.ViewHolder> {
    private List<Event> eventList;
    private Context context;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView eventTimeTextView;
        TextView eventNameTextView;
        TextView eventDescriptionTextView;
        TextView eventLocationTextView;
        ImageButton starImageButton;
        ConstraintLayout constraintLayoutRecyclerView;
        ConstraintLayout constraintLayoutEventOnClick;

        ViewHolder(View parent) {
            super(parent);
            eventTimeTextView = itemView.findViewById(R.id.eventTime);
            eventNameTextView = itemView.findViewById(R.id.eventTitle);
            eventDescriptionTextView = itemView.findViewById(R.id.eventDetail);
            eventLocationTextView = itemView.findViewById(R.id.eventLocation);
            starImageButton = itemView.findViewById(R.id.star);
            constraintLayoutRecyclerView = itemView.findViewById(R.id.constraintLayout);
            constraintLayoutEventOnClick = itemView.findViewById(R.id.constraintLayout_event_onclick);
        }
    }

    EventsAdapter(List<Event> eventsList) {
        eventList = eventsList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent,
                                         int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.schedule_event_list_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        context = parent.getContext();
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Event event = eventList.get(position);

        holder.constraintLayoutEventOnClick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, EventInfoActivity.class);
                intent.putExtra("event_name", event.getName());
                context.startActivity(intent);
            }
        });

        holder.eventTimeTextView.setText(event.getStartTimeOfDay());
        holder.eventNameTextView.setText(event.getName());
        holder.eventDescriptionTextView.setText(event.getDescription());
        holder.eventLocationTextView.setText(event.getLocationDescription());
        holder.starImageButton.setSelected(FavoritesManager.isFavorited(context, event.getName()));

        holder.starImageButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                button.setSelected(!button.isSelected());

                if (button.isSelected()) {
                    FavoritesManager.favoriteEvent(context, event.getName());
                    Snackbar.make(button, R.string.snackbar_notifications_on,
                            Snackbar.LENGTH_SHORT)
                            .show();
                } else {
                    FavoritesManager.unfavoriteEvent(context, event.getName());
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }
}