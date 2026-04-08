# Nanoflow: ACT_SendAllBuyerstoSnowflake

**Allowed Roles:** EcoATM_MDM.Administrator

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Variable**)**
2. **Call Microflow **EcoATM_MDM.SUB_UpdateAllBuyersToSnowflake****
3. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable_2**)**
4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
