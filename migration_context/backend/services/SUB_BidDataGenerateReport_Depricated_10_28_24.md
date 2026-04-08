# Microflow Detailed Specification: SUB_BidDataGenerateReport_Depricated_10_28_24

### 📥 Inputs (Parameters)
- **$Template** (Type: XLSReport.MxTemplate)
- **$BidDataDoc** (Type: AuctionUI.BidDataDoc)

### ⚙️ Execution Flow (Logic Steps)
1. **Create **XLSReport.CustomExcel** (Result: **$NewCustomExcel**)
      - Set **Name** = `$BidDataDoc/Name`
      - Set **DeleteAfterDownload** = `true`**
2. **JavaCallAction**
3. **Update **$NewCustomExcel** (and Save to DB)
      - Set **Name** = `$BidDataDoc/Name`**
4. **DownloadFile**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.