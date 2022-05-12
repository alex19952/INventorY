package amn.inventory;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class AdapterListCategoriesActivity extends RecyclerView.Adapter<AdapterListCategoriesActivity.ListCategoriesiewHolder> {
    ArrayList<String> categories;
    AdapterListCategoriesActivity(ArrayList<String> categories) {
        this.categories = categories;
    }

    public static class ListCategoriesiewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        TextView tittle;
        ListCategoriesiewHolder(View itemView){
            super(itemView);
            tittle = (TextView)itemView.findViewById(R.id.textTittleInRowOfListCategories);
            tittle.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onCardClick(itemView, (String) tittle.getText());
        }
    }

    @Override
    public int getItemCount() {
        return categories.size();
    }

    @Override
    public AdapterListCategoriesActivity.ListCategoriesiewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_for_list_categories_adapter, viewGroup, false);
        AdapterListCategoriesActivity.ListCategoriesiewHolder lc_view_holder = new AdapterListCategoriesActivity.ListCategoriesiewHolder(v);
        return lc_view_holder;
    }

    @Override
    public void onBindViewHolder(AdapterListCategoriesActivity.ListCategoriesiewHolder holder, int i) {
        holder.tittle.setText(categories.get(i));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void swapItem(int fromPosition, int toPosition){
        Collections.swap(this.categories, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }

    private static OnCardClickListener mListener;
    interface OnCardClickListener {
        void onCardClick(View view,  String tittle);
    }
    public void setOnCardClickListener(OnCardClickListener listener) {
        mListener = listener;
    }

}