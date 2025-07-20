package com.example.music;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class AlbumCategoryAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_GRID=0;
    private static final int VIEW_TYPE_CATEGORY=1;

    private Context context;
    private List<AlbumcategoryModel> categoryList;
    private List<AlbumMusicItem> recentItems;

    public AlbumCategoryAdapter(Context context,List<AlbumcategoryModel> categoryList,List<AlbumMusicItem> recentItems){
        this.categoryList=categoryList;
        this.context=context;
        this.recentItems=ensureMinimumRecentItems(recentItems);//ensure atleast 4 items
    }

    public int getItemViewType(int position){
        return position==0?VIEW_TYPE_GRID:VIEW_TYPE_CATEGORY;
    }
    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_GRID) {
            View view=LayoutInflater.from(context).inflate(R.layout.item_grid_container,parent,false);
            return new GridViewHolder(view);
        }else {
            View view = LayoutInflater.from(context).inflate(R.layout.album_item_category, parent, false);
            return new CategoryViewholder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof GridViewHolder){
            GridViewHolder gridViewHolder=(GridViewHolder) holder;
            AlbumGridAdapter gridAdapter=new AlbumGridAdapter(context,recentItems);
            gridViewHolder.recyclerView.setAdapter(gridAdapter);
        } else if(holder instanceof  CategoryViewholder){
            int categoryPosition=position-1; //ensure first item is grid
            AlbumcategoryModel albumcategoryModel=categoryList.get(categoryPosition);
            CategoryViewholder categoryViewholder=(CategoryViewholder) holder;

            categoryViewholder.categoryTitle.setText(albumcategoryModel.getTitle());

            HorizontalAdapater horizontalAdapater=new HorizontalAdapater(context,albumcategoryModel.getItems());
            categoryViewholder.horizontalRecyclerView.setAdapter(horizontalAdapater);

        }

    }

    @Override
    public int getItemCount() {
        return categoryList.size()+1; //=1 for grid section
    }

    public class CategoryViewholder extends RecyclerView.ViewHolder {
        TextView categoryTitle;
        RecyclerView horizontalRecyclerView;
        public CategoryViewholder(@NonNull View itemView) {
            super(itemView);
            categoryTitle=itemView.findViewById(R.id.category_title);
            horizontalRecyclerView=itemView.findViewById(R.id.recyclerViewHorizontal);
            horizontalRecyclerView.setLayoutManager(new LinearLayoutManager(itemView.getContext(),LinearLayoutManager.HORIZONTAL,false));
        }
    }

    private class GridViewHolder extends RecyclerView.ViewHolder {
        RecyclerView recyclerView;

        public GridViewHolder(View itemView) {
            super(itemView);
            recyclerView=itemView.findViewById(R.id.recyclerViewGridContainer);
            recyclerView.setLayoutManager(new GridLayoutManager(itemView.getContext(),2));
        }
    }

    private List<AlbumMusicItem> ensureMinimumRecentItems(List<AlbumMusicItem> recentItems){
        if(recentItems.size()>=4){
            return recentItems;
        }

        List<AlbumMusicItem> tempList=new ArrayList<>(recentItems);
        int index = 0;
        while (tempList.size() < 4) {
            tempList.add(tempList.get(index % recentItems.size())); // Repeat existing songs
            index++;
        }
        return tempList;
    }
}
