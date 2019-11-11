package com.softinator.algolia.adapters;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.ToggleButton;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.softinator.algolia.R;
import com.softinator.algolia.models.Hits;

import java.util.ArrayList;

public abstract class StoriesAdapter extends RecyclerView.Adapter<StoriesAdapter.StoriesAdapterViewHolder>{

    private ArrayList<Hits.Story> stories;
    private Context context;
    private final String Tag = "ADAPTER";

    public StoriesAdapter(Context context, ArrayList<Hits.Story> stories){
        this.context = context;
        this.stories = stories;
    }

    @NonNull
    @Override
    public StoriesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_stories, parent, false);
        StoriesAdapterViewHolder holder = new StoriesAdapterViewHolder(v);
        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull StoriesAdapterViewHolder holder, int position) {
        holder.title.setText(stories.get(position).getTitle());
        holder.createdAt.setText(stories.get(position).getCreatedAtAsString());
        if(holder.storySelected){
            holder.toggle.setChecked(true);
        }else{
            holder.toggle.setChecked(false);
        }

    }

    @Override
    public int getItemCount() {
        return stories.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    public void resetStories(){
        stories.clear();
        notifyDataSetChanged();
    }

    public void addStories(ArrayList<Hits.Story> stories){
        this.stories.addAll(stories);
        notifyDataSetChanged();
    }

    public abstract void onSwitchToggle(boolean state);


    /**
     *
     */
    class StoriesAdapterViewHolder extends RecyclerView.ViewHolder{

        private TextView title;
        private TextView createdAt;
        private Switch toggle;
        private boolean storySelected = false;

        public StoriesAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            title = (TextView) itemView.findViewById(R.id.itemStoriesTitleTV);
            createdAt = (TextView) itemView.findViewById(R.id.itemStoriesCreatedAtTV);
            toggle = (Switch) itemView.findViewById(R.id.itemStoriesSwitch);

            toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    storySelected = isChecked;
                    onSwitchToggle(isChecked);
                }
            });
        }
    }

}
