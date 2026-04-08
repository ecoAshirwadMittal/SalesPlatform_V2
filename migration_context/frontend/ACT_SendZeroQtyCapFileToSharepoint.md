# Nanoflow: ACT_SendZeroQtyCapFileToSharepoint

**Allowed Roles:** EcoATM_Direct_Sharepoint.Administrator

## 📥 Inputs

- **$Auction** (AuctionUI.Auction)
- **$BuyerCode** (EcoATM_BuyerManagement.BuyerCode)

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$ReturnValueName**)**
2. **Call Microflow **EcoATM_Direct_Sharepoint.SUB_SendZeroQtyCapFileToSharepoint** (Result: **$Variable**)**
3. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
4. 🔀 **DECISION:** `$Variable='Success'`
   ➔ **If [true]:**
      1. **Show Message (Information): `File successfully sent to Sharepoint`**
      2. 🏁 **END:** Return empty
   ➔ **If [false]:**
      1. **Show Message (Warning): `File could not be sent to sharepoint - {1}. Please try again.`**
      2. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
