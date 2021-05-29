package peash.esales.App;

public class AppConfig {
    //public static String Base_URL ="http://apps-many.com/";
    public static String Base_URL ="http://192.168.1.222:80/";
    //public static String Base_URL ="http://192.168.1.172/";
    //public static String Base_URL ="http://192.168.0.114/";
    //public static String Base_URL ="http://192.168.0.101/";
    //public static String Base_URL ="http://10.0.2.2:100/";
    public static String URL_AllBank = Base_URL + "api/Bank/Getall";
    public static String URL_AllAccNo = Base_URL + "api/Branch/Getall";
    public static String URL_BankByAccNo = Base_URL + "api/Branch/GetDataByAccNo/";//AccountNo
    public static String URL_AllBranch = Base_URL + "api/Branch/GetAll";
    public static String URL_AllAccHead = Base_URL + "api/SalesInfo/GetAllBankHead/";//CompanyID
    //public static String URL_AllCustomer = Base_URL + "api/CustomerInfo/getall";
    public static String URL_AllCustomer = Base_URL + "api/CustomerInfo/GetAllCustomer";
    public static String URL_CustomerInfoByID = Base_URL + "api/CustomerInfo/GetByID/";//CustomerID
    public static String URL_AllProduct = Base_URL + "api/ProductInfo/getall";
    public static String URL_AllProductForSales = Base_URL + "api/productinfo/getbyproducttypeid/1";
    public static String URL_ProductInfoByID = Base_URL + "api/ProductInfo/GetByID/";//ProductID
    public static String URL_WhouseByProID = Base_URL + "api/SalesInfo/GetWhouseByProID/";//CompanyID/PrudctID
    public static String URL_AllCustomerType = Base_URL + "api/CustomerInfo/GetAllCustomerType";
    public static String URL_StockByProID= Base_URL + "api/PurchaseInfo/GetStockApp/";//ProductID/WarehouseID/
    public static String URL_CusDueInfoByID = Base_URL + "api/SalesInfo/GetCustomerDueApp/";//CompanyID/CustomerID
    public static String URL_SupDueInfoByID = Base_URL + "api/PurchaseInfo/GetCompanyDueApp/";//CompanyID/SupplierID
    public static String URL_Sales = Base_URL + "api/SalesInfo/Add/";
    public static String URL_Warehouse = Base_URL + "api/WareHouse/GetAll/";

    //--------------Order API-----------
    public static String Base_URL1 ="http://bjb.aquivalife.com/";
    public static String URL_Order = Base_URL + "api/OnlineOrder/Add/";
    public static String URL_OrderList = Base_URL + "api/OnlineOrder/GetOrderedCustomerID/";
    public static String URL_OrderDetailsListByID = Base_URL + "api/OnlineOrder/GetOrderedByCustomerID/";//CustomerID//SalesID
    public static String URL_SalesStatusUpdate = Base_URL + "api/OnlineOrder/GetDelivery/";//SalesID//ShippingCost//TotaleAmpunt
    public static String URL_SalesDetailsUpdate = Base_URL + "api/OnlineOrder/UpdateDetails/";//SalesDetailsID/Quantity
    public static String URL_MenuPer = Base_URL + "api/User_Permission/GetMenuPer?email=";//email
    //----------------------------------

    public static String URL_GetByReciveWarehouse = Base_URL + "api/WarehouseSendReciveInfo/GetByReciveWarehouse/";//WarehouseID
    public static String URL_AddWarehouseSend = Base_URL + "api/WarehouseSendReciveInfo/Add";
    public static String URL_CancelWarehouseSend = Base_URL + "api/WarehouseSendReciveInfo/Cancel";
    public static String URL_UpdateWarehouseSend = Base_URL + "api/WarehouseSendReciveInfo/WhSRConByWSRIDModi/";//WSRID/UserEmail

    public static String URL_GetAllWarehouseSendReciveInfo1 = Base_URL + "api/WarehouseSendReciveInfo/GetAllByCompany/1";
    public static String URL_GetAllWarehouseSendReciveInfo = Base_URL + "api/WarehouseSendReciveInfo/GetDetailByWarehouseID/";//WSRID

    public static String URL_ProductModel= Base_URL + "api/Size/GetAllModel/";
    public static String URL_GetPinByModelID= Base_URL + "api/Size/GetAllPinByModelID/";//ModelID
    public static String URL_GetProductByPin= Base_URL + "api/Size/GetProductByPin/";//PinID/ModelID

    public static String URL_GetMeeterByModel= Base_URL + "api/Size/GetAllMetterByModelID/";//ModelID
    public static String GetProductBymeter= Base_URL + "api/Size/GetProductByMetter/";//MeeterID/ModelID
    public static String GetProductByModelPinmeter= Base_URL + "api/Size/GetProductByModelPinMetter/";//PinID/MeeterID/ModelID

    public static String URL_AllSupplier = Base_URL + "api/SupplierInfo/GetAll/";
    public static String URL_SupplierInfoByID = Base_URL + "api/SupplierInfo/GetByID/";//SupplierID
    public static String URL_ProductDetails = Base_URL + "api/PurchaseInfo/GetByProductDetails/";//ProductID
    public static String URL_Purchase = Base_URL + "api/PurchaseInfo/add/";

    public static String URL_AccType = Base_URL + "api/AccType/GetAll/";
    public static String URL_AccGroup = Base_URL + "api/AccGroup/GetByTypeID/";//AccTypeID
    public static String URL_AccHead = Base_URL + "api/AccHead/GetByGroupID/";//AccGroupID

    public static String URL_Login = Base_URL + "api/Others?";
    public static String URL_SalerSignByID = Base_URL + "api/salesinfo/getbyid/";//salesID

    public static String URL_AddGeneralLedger = Base_URL + "api/GeneralLedger/Add/";
    public static String URL_AddPaymentReceived = Base_URL + "api/PaymentReceived/add/";
    public static String URL_AddCustomerInfo = Base_URL + "api/CustomerInfo/AddApp";


    public static String URL_GetInvoiceNo = Base_URL + "api/SalesReturnInfo/GetInvoiceNo/";//CustomerID/CompanyID
    public static String URL_GetProductBySalesID = Base_URL + "api/SalesReturnInfo/GetByInvoiceID/";//SalesID
    public static String URL_GetProductInfo = Base_URL + "api/SalesReturnInfo/GetByProductID/";//productID/SalesID
    public static String URL_SalesReturn = Base_URL + "api/SalesReturnInfo/add";

}
