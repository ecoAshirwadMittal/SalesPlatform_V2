# Nanoflow: NAN_SalesRepresentative_SynchronizeToSnowflake

**Allowed Roles:** EcoATM_BuyerManagement.Administrator

## ⚙️ Execution Flow

1. **Call JS Action **NanoflowCommons.ShowProgress** (Result: **$Variable**)**
2. **DB Retrieve **EcoATM_BuyerManagement.SalesRepresentative**  (Result: **$SalesRepresentativeList**)**
3. **Call Microflow **EcoATM_MDM.SUB_SendAllSalesRepresentativeToSnowflake****
4. **Call JS Action **NanoflowCommons.HideProgress** (Result: **$Variable_2**)**
5. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
