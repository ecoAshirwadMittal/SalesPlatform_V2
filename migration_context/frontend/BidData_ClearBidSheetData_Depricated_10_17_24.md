# Nanoflow: BidData_ClearBidSheetData_Depricated_10_17_24

**Allowed Roles:** EcoATM_BidData.User

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowConfirmation** (Result: **$ConfirmDelete**)**
2. 🔀 **DECISION:** `$ConfirmDelete`
   ➔ **If [true]:**
      1. **Call Microflow **EcoATM_BidData.BidData_DeleteAll** (Result: **$BidDataListCount**)**
      2. **Show Message (Information): `{1} Bid Sheet Data Records Deleted`**
      3. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
