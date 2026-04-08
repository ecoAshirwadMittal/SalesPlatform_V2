# Microflow Analysis: SUB_SendOnlyRMADetailsToSnowflake

### Requirements (Inputs):
- **$RMA** (A record of type: EcoATM_RMA.RMA)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"
      - Store the result in a new variable called **$Log****
4. **Search the Database for **System.User** using filter: { [id=$RMA/System.owner]
 } (Call this list **$User**)**
5. **Update the **$undefined** (Object):
      - Change [EcoATM_RMA.RMA.EntityOwner] to: "if $User!=empty then $User/Name
else
$currentUser/Name
"
      - Change [EcoATM_RMA.RMA.EntityChanger] to: "'Deposco'"
      - Change [EcoATM_RMA.RMA.SystemStatus] to: "$RMA/EcoATM_RMA.RMA_RMAStatus/EcoATM_RMA.RMAStatus/SystemStatus"
      - **Save:** This change will be saved to the database immediately.**
6. **Export Xml** ⚠️ *(This step has a safety catch if it fails)*
7. **Create Variable**
8. **Create List
      - Store the result in a new variable called **$StoreProcedureArgumentList****
9. **Create Object
      - Store the result in a new variable called **$ArgumentJSON_CONTENT****
10. **Change List**
11. **Create Variable**
12. **Java Action Call
      - Store the result in a new variable called **$IsSuccess**** ⚠️ *(This step has a safety catch if it fails)*
13. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
