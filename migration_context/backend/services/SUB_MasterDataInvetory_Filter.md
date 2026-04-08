# Microflow Detailed Specification: SUB_MasterDataInvetory_Filter

### 📥 Inputs (Parameters)
- **$MasterDeviceInventoryList** (Type: EcoATM_Integration.MasterDeviceInventory)

### ⚙️ Execution Flow (Logic Steps)
1. **List Operation: **Head** on **$undefined** (Result: **$FirstMasterDeviceInventory**)**
2. **List Operation: **Sort** on **$undefined** sorted by: ECOATM_CODE (Descending) (Result: **$DescMasterDeviceInventoryList**)**
3. **List Operation: **Head** on **$undefined** (Result: **$LastMasterDeviceInventory**)**
4. **DB Retrieve **EcoATM_MDM.MasterDeviceInventory** Filter: `[ECOATM_CODE>=$FirstMasterDeviceInventory/ECOATM_CODE] [ECOATM_CODE<=$LastMasterDeviceInventory/ECOATM_CODE]` (Result: **$ExistingMasterDeviceInventoryList**)**
5. **CreateList**
6. 🔄 **LOOP:** For each **$IteratorMasterDeviceInventory** in **$MasterDeviceInventoryList**
   │ 1. **List Operation: **Find** on **$undefined** where `$IteratorMasterDeviceInventory/ECOATM_CODE` (Result: **$FoundMasterDeviceInventory**)**
   │ 2. 🔀 **DECISION:** `$FoundMasterDeviceInventory=empty`
   │    ➔ **If [true]:**
   │       1. **Add **$$IteratorMasterDeviceInventory
** to/from list **$FinalMasterDeviceInventoryList****
   │    ➔ **If [false]:**
   └─ **End Loop**
7. 🏁 **END:** Return `$FinalMasterDeviceInventoryList`

**Final Result:** This process concludes by returning a [List] value.