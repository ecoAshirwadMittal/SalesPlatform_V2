# Microflow Analysis: ACT_OrderStatus_ExportJSON

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **EcoATM_PWS.OrderStatus** using filter: { Show everything } (Call this list **$OrderStatusList**)**
3. **Decision:** "has content?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Create Object
      - Store the result in a new variable called **$NewManageFileDocument****
5. **Export Xml** ⚠️ *(This step has a safety catch if it fails)*
6. **Download File**
7. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
