# Nanoflow: ACT_LoadMasterDataInventory

**Allowed Roles:** EcoATM_MDM.Administrator, EcoATM_MDM.SalesLeader, EcoATM_MDM.SalesOps, EcoATM_MDM.SalesRep

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$ReturnValueName**)**
2. **Call Microflow **EcoATM_MDM.SCH_UpdateMasterDeviceInventory****
3. **Open Page: **EcoATM_MDM.PG_MasterDataInventory****
4. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable**)**
5. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
