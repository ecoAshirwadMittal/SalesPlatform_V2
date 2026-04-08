# Microflow Detailed Specification: ACT_GetBuyerCodes_SalesRep_2

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **AuctionUI.SchedulingAuction** Filter: `[RoundStatus='Started']` (Result: **$StartedSchedulingAuction**)**
2. 🔀 **DECISION:** `$StartedSchedulingAuction/Round = 2`
   ➔ **If [false]:**
      1. **DB Retrieve **AuctionUI.BuyerCode** Filter: `[AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/Status = 'Active']` (Result: **$AllActiveBuyerCodeList**)**
      2. **Create **EcoATM_Caching.Parent_NPBuyerCodeSelectHelper** (Result: **$NewParent_NPBuyerCodeSelectHelper**)**
      3. **CreateList**
      4. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$AllActiveBuyerCodeList**
         │ 1. **Create **EcoATM_Caching.NP_BuyerCodeSelect_Helper** (Result: **$NewNP_BuyerCodeSelect_Helper**)
      - Set **Code** = `$IteratorBuyerCode/Code`
      - Set **CompanyName** = `$IteratorBuyerCode/AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/CompanyName`
      - Set **NP_BuyerCodeSelect_Helper_BuyerCode** = `$IteratorBuyerCode`
      - Set **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** = `$NewParent_NPBuyerCodeSelectHelper`
      - Set **comboBoxSearchHelper** = `$IteratorBuyerCode/Code +' '+$IteratorBuyerCode/AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/CompanyName`**
         │ 2. **Add **$$NewNP_BuyerCodeSelect_Helper** to/from list **$NP_BuyerCodeSelect_HelperList_R1R3****
         └─ **End Loop**
      5. 🏁 **END:** Return `$NP_BuyerCodeSelect_HelperList_R1R3`
   ➔ **If [true]:**
      1. **Call Microflow **AuctionUI.ACT_Round2BuyerCodes_SalesRep** (Result: **$Round2BuyerCodeList**)**
      2. **Create **EcoATM_Caching.Parent_NPBuyerCodeSelectHelper** (Result: **$NewParent_NPBuyerCodeSelectHelper_R2**)**
      3. **CreateList**
      4. 🔄 **LOOP:** For each **$IteratorBuyerCode_R2** in **$Round2BuyerCodeList**
         │ 1. **Create **EcoATM_Caching.NP_BuyerCodeSelect_Helper** (Result: **$NewNP_BuyerCodeSelect_Helper_R2**)
      - Set **Code** = `$IteratorBuyerCode_R2/Code`
      - Set **CompanyName** = `$IteratorBuyerCode_R2/AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/CompanyName`
      - Set **NP_BuyerCodeSelect_Helper_BuyerCode** = `$IteratorBuyerCode_R2`
      - Set **NP_BuyerCodeSelect_Helper_Parent_NPBuyerCodeSelectHelper** = `$NewParent_NPBuyerCodeSelectHelper_R2`
      - Set **comboBoxSearchHelper** = `$IteratorBuyerCode_R2/Code +' '+ $IteratorBuyerCode_R2/AuctionUI.BuyerCode_Buyer/AuctionUI.Buyer/CompanyName`**
         │ 2. **Add **$$NewNP_BuyerCodeSelect_Helper_R2** to/from list **$NP_BuyerCodeSelect_HelperList_Round2****
         └─ **End Loop**
      5. 🏁 **END:** Return `$NP_BuyerCodeSelect_HelperList_Round2`

**Final Result:** This process concludes by returning a [List] value.