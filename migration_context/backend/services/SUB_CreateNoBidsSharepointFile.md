# Microflow Detailed Specification: SUB_CreateNoBidsSharepointFile

### 📥 Inputs (Parameters)
- **$BuyerCode** (Type: EcoATM_BuyerManagement.BuyerCode)
- **$MxTemplate** (Type: XLSReport.MxTemplate)
- **$NewAllBidsDoc** (Type: AuctionUI.AllBidsDoc)

### ⚙️ Execution Flow (Logic Steps)
1. **JavaCallAction**
2. 🏁 **END:** Return `$Document`

**Final Result:** This process concludes by returning a [Void] value.