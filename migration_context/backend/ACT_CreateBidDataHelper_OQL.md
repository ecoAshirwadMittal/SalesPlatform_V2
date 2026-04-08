# Microflow Detailed Specification: ACT_CreateBidDataHelper_OQL

### 📥 Inputs (Parameters)
- **$BuyerCodeSelect_Helper** (Type: AuctionUI.BuyerCodeSelect_Helper)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **AuctionUI.SUB_GetCurrentWeek** (Result: **$Week**)**
2. **Create Variable **$oqlQuery** = `'SELECT AI.ecoID AS EcoID, AI.Brand, AI.Model, AI.Model_Name, AI.ecoGrade, AI.Carrier, 0 AS MaximumQuantity, 0 AS TargetPrice, 0 AS BidQuantity, 0 AS BidAmount, ''John'' AS User, ''Code Blue'' AS Code, ''Company XyZ'' AS CompanyName FROM AuctionUI.AgregatedInventory AI'`**
3. **JavaCallAction**
4. 🏁 **END:** Return `$response`

**Final Result:** This process concludes by returning a [List] value.