package peash.esales.Models;

public class PurchaseDetails {
    public static final String TAG= PurchaseDetails.class.getSimpleName();
    public static final String TABLE="PurchaseDetails";

    public static final String KEY_SLID ="SLID";
    public static final String KEY_ProductName="ProductName";
    public static final String KEY_ProductID="ProductID";
    public static final String KEY_WarehouseID="WarehouseID";
    public static final String KEY_Price="Price";
    public static final String KEY_Quantity="Quantity";
    public static final String KEY_Total="Total";

    private String SLID;
    private String ProductName;
    private String ProductID;
    private String WarehouseID;
    private String Price;
    private String Quantity;
    private String Total;

    public String getSLID() {
        return SLID;
    }

    public String getProductName() {
        return ProductName;
    }

    public String getProductID() {
        return ProductID;
    }

    public String getWarehouseID() {
        return WarehouseID;
    }

    public String getPrice() {
        return Price;
    }

    public String getQuantity() {
        return Quantity;
    }

    public String getTotal() {
        return Total;
    }

    public void setSLID(String SLID) {
        this.SLID = SLID;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public void setWarehouseID(String warehouseID) {
        WarehouseID = warehouseID;
    }

    public void setPrice(String price) {
        Price = price;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }

    public void setTotal(String total) {
        Total = total;
    }
}
