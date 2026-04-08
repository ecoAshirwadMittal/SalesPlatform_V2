# Microflow Analysis: ACT_PWSInventory_Confidential

### Execution Steps:
1. **Create Variable**
2. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
3. **Search the Database for **EcoATM_PWSMDM.Device** using filter: { [ATPQty!= empty and ATPQty> 0 and IsActive]
[ItemType='PWS']
 } (Call this list **$DeviceList**)**
4. **Search the Database for **EcoATM_PWSMDM.CaseLot** using filter: { [CaseLotATPQty!= empty and CaseLotATPQty> 0]
[EcoATM_PWSMDM.CaseLot_Device/EcoATM_PWSMDM.Device/ItemType='SPB']
[EcoATM_PWSMDM.CaseLot_Device/EcoATM_PWSMDM.Device/IsActive]
 } (Call this list **$CaseLotList**)**
5. **Search the Database for **XLSReport.MxTemplate** using filter: { [Name = 'PWSInventory']
 } (Call this list **$MxTemplate**)**
6. **Create Object
      - Store the result in a new variable called **$NewOrderDataDoc****
7. **Create List
      - Store the result in a new variable called **$OrderDataExcelList****
8. **Create Variable**
9. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
10. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
11. **Permanently save **$undefined** to the database.**
12. **Create Variable**
13. **Run another process: "EcoATM_PWS.SUB_OrderData_GenerateReport"** ⚠️ *(This step has a safety catch if it fails)*
14. **Delete**
15. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
16. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
