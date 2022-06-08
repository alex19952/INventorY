package amn.inventory.adapters;
import android.annotation.SuppressLint;
import android.database.Cursor;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import amn.inventory.R;
import amn.inventory.helpers.DatabaseStructure;


public class AdapterScanActivity extends RecyclerView.Adapter<AdapterScanActivity.AdapterViewHolder> {
    Cursor cursor;

    public AdapterScanActivity(Cursor cursor) {
        this.cursor = cursor;
    }

    public static class AdapterViewHolder extends  RecyclerView.ViewHolder implements View.OnClickListener{
        TextView id;
        TextView title;
        TextView quantity;
        TextView scanned_quantity;
        ImageView start_siber_view;
        ImageView siber_view;
        ImageView end_siber_view;
        View layout;

        AdapterViewHolder(View itemView){
            super(itemView);
            id = (TextView)itemView.findViewById(R.id.numIDInRecyclerView);
            title = (TextView)itemView.findViewById(R.id.textTittleInRecyclerView);
            quantity = (TextView)itemView.findViewById(R.id.textQuantityInRecyclerView);
            scanned_quantity = (TextView)itemView.findViewById(R.id.textCurrentQuantityInRecyclerView);
            start_siber_view = (ImageView)itemView.findViewById(R.id.rightIdIcon);
            siber_view = (ImageView)itemView.findViewById(R.id.siberIdIcon);
            end_siber_view = (ImageView) itemView.findViewById(R.id.siberEndIdIcon);
            layout = (View) itemView.findViewById(R.id.rowOfScanAdapter);
            layout.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            mListener.onCardClick(itemView, (String) id.getText());
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    @NonNull
    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_for_scan_adapter, viewGroup, false);
        return new AdapterViewHolder(v);
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder viewHolder, int i) {

        cursor.moveToPosition(i);
        int index = cursor.getColumnIndex(DatabaseStructure.DataTable.Columns.search_id);
        viewHolder.id.setText(String.valueOf(cursor.getInt(index)));

        index = cursor.getColumnIndex(DatabaseStructure.DataTable.Columns.title);
        viewHolder.title.setText(cursor.getString(index));

        index = cursor.getColumnIndex(DatabaseStructure.DataTable.Columns.quantity);
        viewHolder.quantity.setText(String.valueOf(cursor.getInt(index)));

        index = cursor.getColumnIndex(DatabaseStructure.DataTable.Columns.scanned_quantity);
        viewHolder.scanned_quantity.setText(String.valueOf(cursor.getInt(index)));

        index = cursor.getColumnIndex(DatabaseStructure.DataTable.Columns.result);
        int result = cursor.getInt(index);
        if (result == 0) {
            viewHolder.start_siber_view.setImageResource(R.drawable.right_id_icon_green);
            viewHolder.siber_view.setImageResource(R.drawable.saber_id_icon_green);
            viewHolder.end_siber_view.setImageResource(R.drawable.saber_end_id_icon_green);
        } else if (result < 0) {
            viewHolder.start_siber_view.setImageResource(R.drawable.right_id_icon_red);
            viewHolder.siber_view.setImageResource(R.drawable.saber_id_icon_red);
            viewHolder.end_siber_view.setImageResource(R.drawable.saber_end_id_icon_red);
        } else {
            viewHolder.start_siber_view.setImageResource(R.drawable.right_id_icon_gray);
            viewHolder.siber_view.setImageResource(R.drawable.saber_id_icon_gray);
            viewHolder.end_siber_view.setImageResource(R.drawable.saber_end_id_icon_gray);
        }
    }

    @Override
    public void onAttachedToRecyclerView(@NonNull RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void changeAdapter(Cursor cursor){
        this.cursor = cursor;
        this.notifyDataSetChanged();
    }

    private static AdapterScanActivity.OnCardClickListener mListener;
    public interface OnCardClickListener {
        void onCardClick(View view,  String id);
    }

    public void setOnCardClickListener(AdapterScanActivity.OnCardClickListener listener) {
        mListener = listener;
    }
}