package amn.inventory;
import android.view.LayoutInflater;
import android.view.View;

import android.support.v7.widget.RecyclerView;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class AdapterScanActivity extends RecyclerView.Adapter<AdapterScanActivity.MTRViewHolder> {
    List<MTR> MTRs;

    AdapterScanActivity(List<MTR> MTRs) {
        this.MTRs = MTRs;
    }

    public static class MTRViewHolder extends  RecyclerView.ViewHolder{
        TextView id;
        TextView tittle;
        TextView quantity;
        TextView current_quantity;

        MTRViewHolder(View itemView){
            super(itemView);
            id = (TextView)itemView.findViewById(R.id.textIDInRecyclerView);
            tittle = (TextView)itemView.findViewById(R.id.textTittleInRecyclerView);
            quantity = (TextView)itemView.findViewById(R.id.textQuantityInRecyclerView);
            current_quantity = (TextView)itemView.findViewById(R.id.textCurrentQuantityInRecyclerView);
        }
    }

    @Override
    public int getItemCount() {
        return MTRs.size();
    }

    @Override
    public MTRViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_for_scan_adapter, viewGroup, false); /////////// !!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!! activity_main?
        MTRViewHolder MTRvh = new MTRViewHolder(v);
        return MTRvh;
    }

    @Override
    public void onBindViewHolder(MTRViewHolder MTRViewHolder, int i) {
            MTRViewHolder.id.setText("id: " + MTRs.get(i).id.toString());
            MTRViewHolder.tittle.setText(MTRs.get(i).tittle);
            MTRViewHolder.quantity.setText(MTRs.get(i).quantity.toString());
            MTRViewHolder.current_quantity.setText(MTRs.get(i).current_quantity.toString());
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }

    public void swapItem(int fromPosition, int toPosition){
        Collections.swap(this.MTRs, fromPosition, toPosition);
        notifyItemMoved(fromPosition, toPosition);
    }
}