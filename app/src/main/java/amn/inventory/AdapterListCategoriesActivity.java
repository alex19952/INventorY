package amn.inventory;

import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;

public class AdapterListCategoriesActivity extends RecyclerView.Adapter<AdapterListCategoriesActivity.ListCategoriesiewHolder> {

    private Cursor cursor;

    AdapterListCategoriesActivity(Cursor cursor) {
        this.cursor = cursor;
    }

    public static class ListCategoriesiewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        TextView tittle;
        TextView quantity;
        View layout;

        ListCategoriesiewHolder(View itemView){

            super(itemView);
            layout = (View)itemView.findViewById(R.id.rowOfListCategoriesAdapter);
            tittle = (TextView)itemView.findViewById(R.id.textTittleInRowOfListCategories);
            quantity = (TextView)itemView.findViewById(R.id.textQuantityPositions);
            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onCardClick(itemView, (String) tittle.getText());
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    @Override
    public AdapterListCategoriesActivity.ListCategoriesiewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_for_list_categories_adapter, viewGroup, false);
        AdapterListCategoriesActivity.ListCategoriesiewHolder lc_view_holder = new AdapterListCategoriesActivity.ListCategoriesiewHolder(v);
        return lc_view_holder;
    }

    @Override
    public void onBindViewHolder(AdapterListCategoriesActivity.ListCategoriesiewHolder holder, int i) {
        cursor.moveToPosition(i);
        holder.tittle.setText(cursor.getString(1));
        holder.quantity.setText(Integer.toString(cursor.getInt(2)));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private static OnCardClickListener mListener;
    interface OnCardClickListener {
        void onCardClick(View view,  String tittle);
    }

    public void setOnCardClickListener(OnCardClickListener listener) {
        mListener = listener;
    }

    public void changeAdapter(Cursor cursor){
        this.cursor = cursor;
        this.notifyDataSetChanged();
    }

}