# Microflow Detailed Specification: SUB_ProcessLastWeekBids

### 📥 Inputs (Parameters)
- **$BidRound** (Type: AuctionUI.BidRound)
- **$NP_BuyerCodeSelect_Helper** (Type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$Last_Week** (Type: EcoATM_MDM.Week)

### ⚙️ Execution Flow (Logic Steps)
1. **LogMessage**
2. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$NP_BuyerCodeSelect_Helper/Code and AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round=1]/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week=$Last_Week and AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted=true]` (Result: **$Round1_BidDataList**)**
3. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$NP_BuyerCodeSelect_Helper/Code and AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round=2]/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week=$Last_Week and AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted=true]` (Result: **$Round2_BidDataList**)**
4. **DB Retrieve **AuctionUI.BidData** Filter: `[AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code=$NP_BuyerCodeSelect_Helper/Code and AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction[Round=3]/AuctionUI.SchedulingAuction_Auction/AuctionUI.Auction/AuctionUI.Auction_Week=$Last_Week and AuctionUI.BidData_BidRound/AuctionUI.BidRound/Submitted=true]` (Result: **$Round3_BidDataList**)**
5. **CreateList**
6. 🔀 **DECISION:** `$Round3_BidDataList=empty`
   ➔ **If [true]:**
      1. **LogMessage**
      2. 🔀 **DECISION:** `$Round2_BidDataList=empty`
         ➔ **If [true]:**
            1. **LogMessage**
            2. **Call Microflow **EcoATM_BidData.ACT_AddCarryOverBidData****
            3. **LogMessage**
            4. 🏁 **END:** Return `$Round1_BidDataList`
         ➔ **If [false]:**
            1. **LogMessage**
            2. 🔄 **LOOP:** For each **$IteratorRound2BidData** in **$Round2_BidDataList**
               │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID= $IteratorRound2BidData/EcoID and $currentObject/Merged_Grade= $IteratorRound2BidData/Merged_Grade` (Result: **$FindInRound1_BidData**)**
               │ 2. 🔀 **DECISION:** `$FindInRound1_BidData=empty`
               │    ➔ **If [false]:**
               │       1. **Remove **$$FindInRound1_BidData** to/from list **$Round1_BidDataList****
               │       2. **Update **$FindInRound1_BidData**
      - Set **BidQuantity** = `$IteratorRound2BidData/BidQuantity`
      - Set **BidAmount** = `$IteratorRound2BidData/BidAmount`**
               │       3. **Add **$$FindInRound1_BidData** to/from list **$LastWeekBidData****
               │    ➔ **If [true]:**
               └─ **End Loop**
            3. **Add **$$Round1_BidDataList** to/from list **$LastWeekBidData****
            4. **Call Microflow **EcoATM_BidData.ACT_AddCarryOverBidData****
            5. **LogMessage**
            6. 🏁 **END:** Return `$Round1_BidDataList`
   ➔ **If [false]:**
      1. **LogMessage**
      2. 🔄 **LOOP:** For each **$IteratorRound3BidData** in **$Round3_BidDataList**
         │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID= $IteratorRound3BidData/EcoID and $currentObject/Merged_Grade= $IteratorRound3BidData/Merged_Grade` (Result: **$FindInRound1_BidData_r3**)**
         │ 2. 🔀 **DECISION:** `$FindInRound1_BidData_r3=empty`
         │    ➔ **If [false]:**
         │       1. **Remove **$$FindInRound1_BidData_r3** to/from list **$Round1_BidDataList****
         │       2. **Update **$FindInRound1_BidData_r3**
      - Set **BidQuantity** = `$IteratorRound3BidData/BidQuantity`
      - Set **BidAmount** = `$IteratorRound3BidData/BidAmount`**
         │       3. **Add **$$FindInRound1_BidData_r3** to/from list **$LastWeekBidData****
         │       4. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID= $IteratorRound3BidData/EcoID and $currentObject/Merged_Grade= $IteratorRound3BidData/Merged_Grade` (Result: **$FindInRound2_BidData_r3**)**
         │       5. 🔀 **DECISION:** `$FindInRound1_BidData_r3=empty`
         │          ➔ **If [false]:**
         │             1. **Remove **$$FindInRound2_BidData_r3** to/from list **$Round2_BidDataList****
         │          ➔ **If [true]:**
         │    ➔ **If [true]:**
         └─ **End Loop**
      3. 🔀 **DECISION:** `$Round2_BidDataList=empty`
         ➔ **If [false]:**
            1. **LogMessage**
            2. 🔄 **LOOP:** For each **$IteratorRound2WBidData** in **$Round2_BidDataList**
               │ 1. **List Operation: **FindByExpression** on **$undefined** where `$currentObject/EcoID= $IteratorRound2WBidData/EcoID and $currentObject/Merged_Grade= $IteratorRound2WBidData/Merged_Grade` (Result: **$FindInRound1_BidData_r2**)**
               │ 2. 🔀 **DECISION:** `$FindInRound1_BidData_r2=empty`
               │    ➔ **If [false]:**
               │       1. **Remove **$$FindInRound1_BidData_r2** to/from list **$Round1_BidDataList****
               │       2. **Update **$FindInRound1_BidData_r2**
      - Set **BidQuantity** = `$IteratorRound2WBidData/BidQuantity`
      - Set **BidAmount** = `$IteratorRound2WBidData/BidAmount`**
               │       3. **Add **$$FindInRound1_BidData_r2** to/from list **$LastWeekBidData****
               │    ➔ **If [true]:**
               └─ **End Loop**
            3. **Add **$$Round1_BidDataList** to/from list **$LastWeekBidData****
            4. **Call Microflow **EcoATM_BidData.ACT_AddCarryOverBidData****
            5. **LogMessage**
            6. 🏁 **END:** Return `$Round1_BidDataList`
         ➔ **If [true]:**
            1. **LogMessage**
            2. **Add **$$Round1_BidDataList** to/from list **$LastWeekBidData****
            3. **Call Microflow **EcoATM_BidData.ACT_AddCarryOverBidData****
            4. **LogMessage**
            5. 🏁 **END:** Return `$Round1_BidDataList`

**Final Result:** This process concludes by returning a [List] value.