package peash.esales.Models;

public class OrderDetails {
    public static final String TAG= OrderDetails.class.getSimpleName();
    public static final String TABLE="OrderDetails";

    public static final String KEY_SLID ="SLID";
    public static final String KEY_ProductName="ProductName";
    public static final String KEY_ProductID="ProductID";
    public static final String KEY_WarehouseID="WarehouseID";
    public static final String KEY_OpeningQty="OpeningQty";
    public static final String KEY_ReturnQty="ReturnQty";
    public static final String KEY_Price="Price";
    public static final String KEY_Quantity="Quantity";
    public static final String KEY_Total="Total";

    private String SLID;
    private String ProductName;
    private String ProductID;
    private String WarehouseID;
    private String OpeningQty;
    private String ReturnQty;
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

    public String getOpeningQty() {
        return OpeningQty;
    }

    public String getReturnQty() {
        return ReturnQty;
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

    public void setOpeningQty(String openingQty) {
        OpeningQty = openingQty;
    }

    public void setReturnQty(String returnQty) {
        ReturnQty = returnQty;
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
