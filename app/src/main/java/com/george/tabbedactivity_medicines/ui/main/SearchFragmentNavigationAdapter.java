package com.george.tabbedactivity_medicines.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.george.tabbedactivity_medicines.R;

import java.util.ArrayList;

public class SearchFragmentNavigationAdapter extends RecyclerView.Adapter<SearchFragmentNavigationAdapter.NavigationAdapterViewHolder> {

    private Context mContext;
    private ArrayList<String> hitsList;
    private SearchClickItemListener mSearchClickItemListener;

    SearchFragmentNavigationAdapter(Context context, ArrayList<String> list, SearchClickItemListener listener) {
        mContext = context;
        hitsList = list;
        mSearchClickItemListener = listener;
    }

    public interface SearchClickItemListener {
        void onListItemClick(int itemIndex, ImageView sharedImage, String type);
    }

    @NonNull
    @Override
    public NavigationAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
        return new NavigationAdapterViewHolder(LayoutInflater.from(parent.getContext())
                .inflate(R.layout.search_fragment_adapter, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull NavigationAdapterViewHolder holder, int position) {

        //setting the name at the textView
        holder.textViewHolder.setText(hitsList.get(position));
        holder.imageViewHolder.setImageDrawable(mContext.getResources().
                getDrawable(R.drawable.medicine));

    }

    @Override
    public int getItemCount() {
        if (hitsList != null && hitsList.size() > 0) {
            return hitsList.size();
        } else {
            return 0;
        }
    }

    class NavigationAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView textViewHolder;
        private ImageView imageViewHolder;

        public NavigationAdapterViewHolder(View itemView) {
            super(itemView);
            textViewHolder = itemView.findViewById(R.id.textViewFragmentAdapter);
            imageViewHolder = itemView.findViewById(R.id.imageFragmentAdapter);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int clickedPosition = getAdapterPosition();
            mSearchClickItemListener.onListItemClick(clickedPosition, imageViewHolder, hitsList.get(clickedPosition));
        }
    }

    public void setHitsData(ArrayList<String> list) {
        hitsList = list;
        /*notifyDataSetChanged();*/
    }
}
