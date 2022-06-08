package amn.inventory.adapters;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import amn.inventory.R;
import amn.inventory.helpers.DatabaseStructure;

public class AdapterCategoriesActivity extends RecyclerView.Adapter<AdapterCategoriesActivity.ListCategoriesiewHolder> {

    private Cursor cursor;

    public AdapterCategoriesActivity(Cursor cursor) {
        this.cursor = cursor;
    }

    public static class ListCategoriesiewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{

        TextView name;
        TextView quantity;
        ImageView completeIcon;
        ImageView wrongIcon;
        View layout;

        ListCategoriesiewHolder(View itemView){

            super(itemView);
            layout = (View)itemView.findViewById(R.id.rowOfListCategoriesAdapter);
            name = (TextView)itemView.findViewById(R.id.textTittleInRowOfListCategories);
            quantity = (TextView)itemView.findViewById(R.id.textQuantityPositions);
            completeIcon = (ImageView)itemView.findViewById(R.id.completeIcon);
            wrongIcon = (ImageView)itemView.findViewById(R.id.wrongIcon);
            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onCardClick(itemView, (String) name.getText());
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    @NonNull
    @Override
    public AdapterCategoriesActivity.ListCategoriesiewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_for_list_categories_adapter, viewGroup, false);
        return new ListCategoriesiewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterCategoriesActivity.ListCategoriesiewHolder holder, int i) {
        cursor.moveToPosition(i);
        int index;
        index = cursor.getColumnIndex(DatabaseStructure.CategoriesTable.Columns.category_name);
        holder.name.setText(cursor.getString(index));

        index = cursor.getColumnIndex(DatabaseStructure.CategoriesTable.Columns.total_quantity);
        holder.quantity.setText(Integer.toString(cursor.getInt(index)));

        index = cursor.getColumnIndex(DatabaseStructure.CategoriesTable.Columns.category_completed);
        if (cursor.getInt(index) == 1) {
            holder.completeIcon.setVisibility(ImageView.VISIBLE);
        }
        else {
            holder.completeIcon.setVisibility(ImageView.GONE);
        }

        index = cursor.getColumnIndex(DatabaseStructure.CategoriesTable.Columns.category_wrong);
        if (cursor.getInt(index) == 1) {
            holder.wrongIcon.setVisibility(ImageView.VISIBLE);
        }
        else {
            holder.wrongIcon.setVisibility(ImageView.GONE);
        }

    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    private static OnCardClickListener mListener;
    public interface OnCardClickListener {
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