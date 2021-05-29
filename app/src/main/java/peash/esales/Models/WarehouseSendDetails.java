package peash.esales.Models;

public class WarehouseSendDetails {
    public static final String TAG= WarehouseSendDetails.class.getSimpleName();
    public static final String TABLE="WarehouseSendDetails";

    public static final String KEY_SLID ="SLID";
    public static final String KEY_Date="Date";
    public static final String KEY_ProductName="ProductName";
    public static final String KEY_ProductID="ProductID";
    public static final String KEY_FromWarehouseID="FromWarehouseID";
    public static final String KEY_ToWarehouseID="ToWarehouseID";
    public static final String KEY_CostPrice="Price";
    public static final String KEY_Qty="Quantity";

    private String SLID;
    private String Date;
    private String ProductName;
    private String ProductID;
    private String FromWarehouseID;
    private String ToWarehouseID;
    private String CostPrice;
    private String Quantity;

    public String getSLID() {
        return SLID;
    }

    public String getDate() {
        return Date;
    }

    public String getProductName() {
        return ProductName;
    }

    public String getProductID() {
        return ProductID;
    }

    public String getFromWarehouseID() {
        return FromWarehouseID;
    }

    public String getToWarehouseID() {
        return ToWarehouseID;
    }

    public String getCostPrice() {
        return CostPrice;
    }

    public String getQuantity() {
        return Quantity;
    }

    public void setSLID(String SLID) {
        this.SLID = SLID;
    }

    public void setDate(String date) {
        Date = date;
    }

    public void setProductName(String productName) {
        ProductName = productName;
    }

    public void setProductID(String productID) {
        ProductID = productID;
    }

    public void setFromWarehouseID(String fromWarehouseID) {
        FromWarehouseID = fromWarehouseID;
    }

    public void setToWarehouseID(String toWarehouseID) {
        ToWarehouseID = toWarehouseID;
    }

    public void setCostPrice(String costPrice) {
        CostPrice = costPrice;
    }

    public void setQuantity(String quantity) {
        Quantity = quantity;
    }
}
