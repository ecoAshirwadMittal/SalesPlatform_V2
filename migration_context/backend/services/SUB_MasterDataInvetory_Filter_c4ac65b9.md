# Microflow Analysis: SUB_MasterDataInvetory_Filter

### Requirements (Inputs):
- **$MasterDeviceInventoryList** (A record of type: EcoATM_Integration.MasterDeviceInventory)

### Execution Steps:
1. **Take the list **$MasterDeviceInventoryList**, perform a [Head], and call the result **$FirstMasterDeviceInventory****
2. **Take the list **$MasterDeviceInventoryList**, perform a [Sort], and call the result **$DescMasterDeviceInventoryList****
3. **Take the list **$DescMasterDeviceInventoryList**, perform a [Head], and call the result **$LastMasterDeviceInventory****
4. **Search the Database for **EcoATM_MDM.MasterDeviceInventory** using filter: { [ECOATM_CODE>=$FirstMasterDeviceInventory/ECOATM_CODE]
[ECOATM_CODE<=$LastMasterDeviceInventory/ECOATM_CODE]
 } (Call this list **$ExistingMasterDeviceInventoryList**)**
5. **Create List
      - Store the result in a new variable called **$FinalMasterDeviceInventoryList****
6. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
