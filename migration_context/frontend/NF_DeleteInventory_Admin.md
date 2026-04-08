# Nanoflow: NF_DeleteInventory_Admin

**Allowed Roles:** AuctionUI.Administrator

## 📥 Inputs

- **$Week** (AuctionUI.Week)

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Variable**)**
2. **Call Microflow **AuctionUI.ACT_DeleteInventoryPlusBidsReportForWeek****
3. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable_2**)**
4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
