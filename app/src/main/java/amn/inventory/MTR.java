package amn.inventory;

class MTR {
    public
    Integer id;
    String account;
    String date;
    String inv_num;
    String tittle;
    Integer quantity;
    String price;
    String place;
    String MOL;
    String description;
    Integer current_quantity;

    MTR(Integer id, String account, String date, String inv_num,
        String tittle, Integer quantity, String price, String place,
        String MOL, String description) {
        this.id = id;
        this.account = account;
        this.date = date;
        this.inv_num = inv_num;
        this.tittle = tittle;
        this.quantity = quantity;
        this.price = price;
        this.place = place;
        this.MOL = MOL;
        this.description = description;
        this.current_quantity = 0;
    }

    MTR(Integer id, String tittle, Integer quantity){
        this(id, "account", "date", "inv_num", tittle, quantity, "price", "place", "MOL", "description");
    }

    public void IncrementCurrentQuantity (){
        this.current_quantity ++;
    }
}

