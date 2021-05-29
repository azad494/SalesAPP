package peash.esales.Models;

public class Purchase {
    public static final String TAG=Purchase.class.getSimpleName();
    public static final String TABLE="Purchase";

    public static final String KEY_SLID ="SLID";
    public static final String KEY_Name ="Name";
    public static final String KEY_Code ="Code";
    public static final String KEY_CostPrice ="CostPrice";
    public static final String KEY_Quantity ="Quantity";
    public static final String KEY_Total="Total";

    ///For save
   // public static final String KEY_PurchaseID="PurchaseID";
    public static final String KEY_SupplierID="SupplierID";
    public static final String KEY_CompanyID="CompanyID ";
    public static final String KEY_PurchaserID="PurchaserID";
    public static final String KEY_InvoiceNo="InvoiceNo";
    public static final String KEY_InvoiceDate="nvoiceDate";
    public static final String KEY_Job_By="Job_By";
    public static final String KEY_Purchase_Date="Purchase_Date";
    public static final String KEY_Sys_Purchase_Date="Sys_Purchase_Date";
    public static final String KEY_Note="Note";
    public static final String KEY_Sys_PaymentID="Sys_PaymentID";
    public static final String KEY_User_PaymentID="User_PaymentID";
    public static final String KEY_Sys_Payment_Date="Sys_Payment_Date";
    public static final String KEY_Payment_Date="Payment_Date";
    public static final String KEY_CustomerID="CustomerID";
    public static final String KEY_DrAmount="DrAmount";
    public static final String KEY_CrAmount="CrAmount";
    public static final String KEY_InvoiceType="InvoiceType";
    public static final String KEY_ChequePayID="ChequePayID";
    public static final String KEY_Issue_Date="Issue_Date";
    public static final String KEY_TrnID="TrnID";
    public static final String KEY_BankID="BankID";
    public static final String KEY_BranchID="BranchID";
    public static final String KEY_ChequeNo="ChequeNo";
    public static final String KEY_Cheque_Amount="Cheque_Amount";
    public static final String KEY_IsVoid="IsVoid";
    public static final String KEY_MaturityDate="MaturityDate";
    public static final String KEY_Status="Status";
    public static final String KEY_PurchaseDetails="PurchaseDetails";
  //  ---------------------------------------------------------
///For details table
    public static  final String KEY_PurchaseDetailID="PurchaseDetailID";
    public static final String KEY_PurchaseID="PurchaseID";
    public static final String KEY_ProductID="ProductID";
    public static final String KEY_ProductName="ProductName";
    public static final String KEY_Rate="Rate";
    public static final String KEY_total="total";
    public static final String KEY_ProductCode="ProductCode";
//-----------------------------------------------------------------------
/// For Field
    private String SLID;
    private String Name;
    private String Code;
    private String CostPrice;
    private String Quantity;
    private String Total;

    private String  SupplierID;
    private String  CompanyID ;
    private String  PurchaserID;
    private String  InvoiceNo;
    private String  nvoiceDate;
    private String  Job_By;
    private String  Purchase_Date;
    private String  Sys_Purchase_D;
    private String  Note;
    private String  Sys_PaymentID;
    private String  User_PaymentID;
    private String  Sys_Payment_Date;
    private String  Payment_Date;
    private String  CustomerID;
    private String  DrAmount;
    private String  CrAmount;
    private String  InvoiceType;
    private String  ChequePayID;
    private String  Issue_Date;
    private String  TrnID;
    private String  BankID;
    private String  BranchID;
    private String  ChequeNo;
    private String  Cheque_Amount;
    private String  sVoid;
    private String  MaturityDate;
    private String  Status;
    private String  PurchaseDetails;
    ///For Details
    private String  PurchaseID;
    private String  ProductID;
    private String  ProductName;
    private String  Rate;
    private String  total;
    private String  ProductCode;
    private String PurchaseDetailID;
//-------------------------------------------------------------
//Get
    public String getSLID() { return SLID; }
    public String getName() { return Name; }
    public String getCode() { return Code; }
    public String getCostPrice() { return CostPrice; }
    public String getQuantity() { return Quantity; }
    public String getTotal() { return Total; }

    public String getSupplierID() { return SupplierID; }
    public String getCompanyID() { return CompanyID; }
    public String getPurchaserID() { return PurchaserID; }
    public String getInvoiceNo() { return InvoiceNo; }
    public String getNvoiceDate() { return nvoiceDate; }
    public String getJob_By() { return Job_By; }
    public String getPurchase_Date() { return Purchase_Date; }
    public String getSys_Purchase_D() { return Sys_Purchase_D; }
    public String getNote() { return Note; }
    public String getSys_PaymentID() { return Sys_PaymentID; }
    public String getUser_PaymentID() { return User_PaymentID; }
    public String getSys_Payment_Date() { return Sys_Payment_Date; }
    public String getPayment_Date() { return Payment_Date; }
    public String getCustomerID() { return CustomerID; }
    public String getDrAmount() { return DrAmount; }
    public String getCrAmount() { return CrAmount; }
    public String getInvoiceType() { return InvoiceType;}
    public String getChequePayID() { return ChequePayID; }
    public String getIssue_Date() { return Issue_Date; }
    public String getTrnID() { return TrnID; }
    public String getBankID() { return BankID; }
    public String getBranchID() { return BranchID; }
    public String getChequeNo() { return ChequeNo; }
    public String getCheque_Amount() { return Cheque_Amount; }
    public String getsVoid() { return sVoid; }
    public String getMaturityDate() { return MaturityDate; }
    public String getStatus() { return Status; }
    public String getPurchaseDetails() { return PurchaseDetails; }
    ///For Details table
    public String getPurchaseID() { return PurchaseID; }
    public String getProductID() { return ProductID; }
    public String getProductName() { return ProductName; }
    public String getRate() { return Rate; }
    public String getProductCode() { return ProductCode; }

    public String getPurchaseDetailID() {
        return PurchaseDetailID;
    }
    //--------------------------------------------------------------------------
///Set method

    public void setSLID(String SLID) { this.SLID = SLID; }
    public void setName(String name) { Name = name; }
    public void setCode(String code) { Code = code; }
    public void setCostPrice(String costPrice) { CostPrice = costPrice; }
    public void setQuantity(String quantity) { Quantity = quantity; }
    public void setTotal(String total) { Total = total; }


    public void setSupplierID(String supplierID) { SupplierID = supplierID; }
    public void setCompanyID(String companyID) { CompanyID = companyID; }
    public void setPurchaserID(String purchaserID) { PurchaserID = purchaserID; }
    public void setInvoiceNo(String invoiceNo) { InvoiceNo = invoiceNo; }
    public void setNvoiceDate(String nvoiceDate) { this.nvoiceDate = nvoiceDate; }
    public void setJob_By(String job_By) { Job_By = job_By; }
    public void setPurchase_Date(String purchase_Date) { Purchase_Date = purchase_Date; }
    public void setSys_Purchase_D(String sys_Purchase_D) { Sys_Purchase_D = sys_Purchase_D; }
    public void setNote(String note) { Note = note; }
    public void setSys_PaymentID(String sys_PaymentID) { Sys_PaymentID = sys_PaymentID; }
    public void setUser_PaymentID(String user_PaymentID) { User_PaymentID = user_PaymentID; }
    public void setSys_Payment_Date(String sys_Payment_Date) { Sys_Payment_Date = sys_Payment_Date; }
    public void setPayment_Date(String payment_Date) { Payment_Date = payment_Date; }
    public void setCustomerID(String customerID) { CustomerID = customerID; }
    public void setDrAmount(String drAmount) { DrAmount = drAmount; }
    public void setCrAmount(String crAmount) { CrAmount = crAmount; }
    public void setInvoiceType(String invoiceType) { InvoiceType = invoiceType; }
    public void setChequePayID(String chequePayID) { ChequePayID = chequePayID; }
    public void setIssue_Date(String issue_Date) { Issue_Date = issue_Date; }
    public void setTrnID(String trnID) { TrnID = trnID; }
    public void setBankID(String bankID) { BankID = bankID; }
    public void setBranchID(String branchID) { BranchID = branchID; }
    public void setChequeNo(String chequeNo) { ChequeNo = chequeNo; }
    public void setCheque_Amount(String cheque_Amount) { Cheque_Amount = cheque_Amount; }
    public void setsVoid(String sVoid) { this.sVoid = sVoid; }
    public void setMaturityDate(String maturityDate) { MaturityDate = maturityDate; }
    public void setStatus(String status) { Status = status; }
    public void setPurchaseDetails(String purchaseDetails) { PurchaseDetails = purchaseDetails; }
    public void setPurchaseID(String purchaseID) { PurchaseID = purchaseID; }
    public void setProductID(String productID) { ProductID = productID; }
    public void setProductName(String productName) { ProductName = productName; }
    public void setRate(String rate) { Rate = rate; }
    public void setProductCode(String productCode) { ProductCode = productCode; }

    public void setPurchaseDetailID(String purchaseDetailID) {
        PurchaseDetailID = purchaseDetailID;
    }
}
