# Microflow Detailed Specification: SUB_SendAllBuyersToSnowflake

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **EcoATM_BuyerManagement.Buyer**  (Result: **$BuyerList**)**
2. **CreateList**
3. **CreateList**
4. 🔄 **LOOP:** For each **$IteratorBuyer** in **$BuyerList**
   │ 1. 🔀 **DECISION:** `$IteratorBuyer/EntityOwner=empty or $IteratorBuyer/EntityChanger=empty`
   │    ➔ **If [true]:**
   │       1. **Call Microflow **EcoATM_MDM.SUB_SetBuyerOwnerAndChanger****
   │       2. **Add **$$IteratorBuyer
** to/from list **$ToBeCommitedBuyerList****
   │       3. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyer** (Result: **$BuyerCodeList**)**
   │       4. 🔀 **DECISION:** `$BuyerCodeList!=empty`
   │          ➔ **If [true]:**
   │             1. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
   │                │ 1. 🔀 **DECISION:** `$IteratorBuyerCode/EntityOwner=empty or $IteratorBuyerCode/EntityChanger=empty`
   │                │    ➔ **If [true]:**
   │                │       1. **Call Microflow **EcoATM_MDM.SUB_SetBuyerCodeOwnerAndChanger****
   │                │       2. **Add **$$IteratorBuyerCode
** to/from list **$ToBeCommitedBuyerCodeList****
   │                │    ➔ **If [false]:**
   │                └─ **End Loop**
   │          ➔ **If [false]:**
   │    ➔ **If [false]:**
   │       1. **Retrieve related **BuyerCode_Buyer** via Association from **$IteratorBuyer** (Result: **$BuyerCodeList**)**
   │       2. 🔀 **DECISION:** `$BuyerCodeList!=empty`
   │          ➔ **If [true]:**
   │             1. 🔄 **LOOP:** For each **$IteratorBuyerCode** in **$BuyerCodeList**
   │                │ 1. 🔀 **DECISION:** `$IteratorBuyerCode/EntityOwner=empty or $IteratorBuyerCode/EntityChanger=empty`
   │                │    ➔ **If [true]:**
   │                │       1. **Call Microflow **EcoATM_MDM.SUB_SetBuyerCodeOwnerAndChanger****
   │                │       2. **Add **$$IteratorBuyerCode
** to/from list **$ToBeCommitedBuyerCodeList****
   │                │    ➔ **If [false]:**
   │                └─ **End Loop**
   │          ➔ **If [false]:**
   └─ **End Loop**
5. 🔀 **DECISION:** `$ToBeCommitedBuyerCodeList!=empty`
   ➔ **If [true]:**
      1. **Commit/Save **$ToBeCommitedBuyerCodeList** to Database**
      2. 🔀 **DECISION:** `$ToBeCommitedBuyerList!=empty`
         ➔ **If [true]:**
            1. **Commit/Save **$ToBeCommitedBuyerList** to Database**
            2. **Call Microflow **EcoATM_MDM.SUB_SendBuyerToSnowflake****
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **EcoATM_MDM.SUB_SendBuyerToSnowflake****
            2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🔀 **DECISION:** `$ToBeCommitedBuyerList!=empty`
         ➔ **If [true]:**
            1. **Commit/Save **$ToBeCommitedBuyerList** to Database**
            2. **Call Microflow **EcoATM_MDM.SUB_SendBuyerToSnowflake****
            3. 🏁 **END:** Return empty
         ➔ **If [false]:**
            1. **Call Microflow **EcoATM_MDM.SUB_SendBuyerToSnowflake****
            2. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.