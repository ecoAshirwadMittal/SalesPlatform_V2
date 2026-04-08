# Microflow Analysis: SUB_UpdateAggegatedInventoryFilterAssociations_Carrier

### Requirements (Inputs):
- **$AggregatedInventory** (A record of type: AuctionUI.AggregatedInventory)
- **$CarrierList** (A record of type: EcoATM_MDM.Carrier)
- **$enum_BuyerCodeType** (A record of type: Object)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Run another process: "EcoATM_BuyerManagement.SUB_GetOrCreateCarrier"
      - Store the result in a new variable called **$Carrier****
5. **Decision:** "Carrier Exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
6. **Update the **$undefined** (Object):
      - Change [EcoATM_MDM.Carrier.IsEnabledForFilter] to: "true"**
7. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggregatedInventory_Carrier] to: "$Carrier"**
8. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
