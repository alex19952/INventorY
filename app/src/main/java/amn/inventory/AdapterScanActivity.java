package amn.inventory;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


public class AdapterScanActivity extends RecyclerView.Adapter<AdapterScanActivity.AdapterViewHolder> {
    Cursor cursor;

    AdapterScanActivity(Cursor cursor) {
        this.cursor = cursor;
    }

    public static class AdapterViewHolder extends  RecyclerView.ViewHolder{
        TextView id;
        TextView tittle;
        TextView quantity;
        TextView current_quantity;
        ImageView start_siber_view;
        ImageView siber_view;
        ImageView end_siber_view;


        AdapterViewHolder(View itemView){
            super(itemView);
            id = (TextView)itemView.findViewById(R.id.numIDInRecyclerView);
            tittle = (TextView)itemView.findViewById(R.id.textTittleInRecyclerView);
            quantity = (TextView)itemView.findViewById(R.id.textQuantityInRecyclerView);
            current_quantity = (TextView)itemView.findViewById(R.id.textCurrentQuantityInRecyclerView);
            start_siber_view = (ImageView)itemView.findViewById(R.id.rightIdIcon);
            siber_view = (ImageView)itemView.findViewById(R.id.siberIdIcon);
            end_siber_view = (ImageView) itemView.findViewById(R.id.siberEndIdIcon);
        }
    }

    @Override
    public int getItemCount() {
        return cursor.getCount();
    }

    @Override
    public AdapterViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_for_scan_adapter, viewGroup, false); /////////// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! activity_main?
        AdapterViewHolder viewHolder = new AdapterViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(AdapterViewHolder viewHolder, int i) {
        cursor.moveToPosition(i);
        int indext = cursor.getInt(1);
        int x = cursor.getInt(3);
        int y = cursor.getInt(4);

        viewHolder.id.setText(String.valueOf(cursor.getInt(1)));
        viewHolder.tittle.setText(cursor.getString(2));

        viewHolder.quantity.setText(String.valueOf(x));
        viewHolder.current_quantity.setText(String.valueOf(y));
//        viewHolder.quantity.setText(String.valueOf(cursor.getInt(3)));
//        viewHolder.current_quantity.setText(String.valueOf(cursor.getInt(4)));

        if (y == x) {
            viewHolder.start_siber_view.setImageResource(R.drawable.right_id_icon_green);
            viewHolder.siber_view.setImageResource(R.drawable.siber_id_icon_green);
            viewHolder.end_siber_view.setImageResource(R.drawable.siber_end_id_icon_green);
        }
        else if (y > x) {
            viewHolder.start_siber_view.setImageResource(R.drawable.right_id_icon_red);
            viewHolder.siber_view.setImageResource(R.drawable.siber_id_icon_red);
            viewHolder.end_siber_view.setImageResource(R.drawable.siber_end_id_icon_red);
        }
        else {
            viewHolder.start_siber_view.setImageResource(R.drawable.right_id_icon_gray);
            viewHolder.siber_view.setImageResource(R.drawable.siber_id_icon_gray);
            viewHolder.end_siber_view.setImageResource(R.drawable.siber_end_id_icon_gray);
        }
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