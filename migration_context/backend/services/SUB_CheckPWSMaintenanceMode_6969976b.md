# Microflow Analysis: SUB_CheckPWSMaintenanceMode

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.MaintenanceMode** using filter: { [IsEnabled and '[%CurrentDateTime%]' > StartTime and '[%CurrentDateTime%]' < EndTime] } (Call this list **$MaintenanceMode**)**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
