package amn.inventory;

import java.util.ArrayList;
import java.util.List;

public class TestList {
    List<MTR> MTRs = new ArrayList<MTR>();
    TestList () {
        for (int i = 1; i < 20; i++){
            MTR mtr = new MTR(i, "название" + i, i % 3 + 1);
            this.MTRs.add(mtr);
        }
    }

    public List<MTR> get_List(){
        return MTRs;
    }
}
