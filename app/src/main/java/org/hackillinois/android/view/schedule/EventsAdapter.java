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
    private List<Event> mEventList;
    private Context context;

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv_eventName;
        TextView tv_eventDescription;
        TextView tv_eventLocation;
        ImageButton imageButton_star;
        ConstraintLayout constraintLayout_recyclerView;
        ConstraintLayout constraintLayout_event_onclick;

        ViewHolder(View parent) {
            super(parent);
            tv_eventName = itemView.findViewById(R.id.eventTitle);
            tv_eventDescription = itemView.findViewById(R.id.eventDetail);
            tv_eventLocation = itemView.findViewById(R.id.eventLocation);
            imageButton_star = itemView.findViewById(R.id.star);
            constraintLayout_recyclerView = itemView.findViewById(R.id.constraintLayout);
            constraintLayout_event_onclick = itemView.findViewById(R.id.constraintLayout_event_onclick);
        }
    }

    EventsAdapter(List<Event> eventsList) {
        mEventList = eventsList;
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
        final Event event = mEventList.get(position);

        holder.constraintLayout_event_onclick.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Context context = v.getContext();
                Intent intent = new Intent(context, EventInfoActivity.class);
                intent.putExtra("event_name", event.getName());
                context.startActivity(intent);
            }
        });

        holder.tv_eventName.setText(event.getName());
        holder.tv_eventDescription.setText(event.getDescription());
        holder.tv_eventLocation.setText(event.getLocationDescription());
        holder.imageButton_star.setSelected(FavoritesManager.isFavorited(context, event.getName()));

        holder.imageButton_star.setOnClickListener(new View.OnClickListener() {
            public void onClick(View button) {
                button.setSelected(!button.isSelected());

                if (button.isSelected()) {
                    FavoritesManager.favoriteEvent(context, event.getName());
                    Snackbar.make(button, R.string.snackbar_notifications_on_text,
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
        return mEventList.size();
    }
}