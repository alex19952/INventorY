package amn.inventory;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;


public class AdapterScanActivity extends RecyclerView.Adapter<AdapterScanActivity.MTRViewHolder> {
    Cursor cursor;

    AdapterScanActivity(Cursor cursor) {
        this.cursor = cursor;
    }

    public static class MTRViewHolder extends  RecyclerView.ViewHolder{
        TextView id;
        TextView tittle;
        TextView quantity;
        TextView current_quantity;

        MTRViewHolder(View itemView){
            super(itemView);
            id = (TextView)itemView.findViewById(R.id.numIDInRecyclerView);
            tittle = (TextView)itemView.findViewById(R.id.textTittleInRecyclerView);
            quantity = (TextView)itemView.findViewById(R.id.textQuantityInRecyclerView);
            current_quantity = (TextView)itemView.findViewById(R.id.textCurrentQuantityInRecyclerView);
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    @Override
    public MTRViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_for_scan_adapter, viewGroup, false); /////////// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! activity_main?
        MTRViewHolder MTRvh = new MTRViewHolder(v);
        return MTRvh;
    }

    @Override
    public void onBindViewHolder(MTRViewHolder MTRViewHolder, int i) {
            cursor.moveToPosition(i);
            MTRViewHolder.id.setText(String.valueOf(cursor.getInt(1)));
            MTRViewHolder.tittle.setText(cursor.getString(2));
            MTRViewHolder.quantity.setText(String.valueOf(cursor.getInt(3)));
            MTRViewHolder.current_quantity.setText(String.valueOf(cursor.getInt(4)));
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void changeAdapter(Cursor cursor){
        this.cursor = cursor;
        this.notifyDataSetChanged();
    }
}