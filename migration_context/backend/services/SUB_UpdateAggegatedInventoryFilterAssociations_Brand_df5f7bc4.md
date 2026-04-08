# Microflow Analysis: SUB_UpdateAggegatedInventoryFilterAssociations_Brand

### Requirements (Inputs):
- **$AggregatedInventory** (A record of type: AuctionUI.AggregatedInventory)
- **$BrandList** (A record of type: EcoATM_MDM.Brand)
- **$enum_BuyerCodeType** (A record of type: Object)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
4. **Run another process: "EcoATM_BuyerManagement.SUB_GetOrCreateBrand"
      - Store the result in a new variable called **$Brand****
5. **Decision:** "Brand Exists"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
6. **Update the **$undefined** (Object):
      - Change [EcoATM_MDM.Brand.IsEnabledForFilter] to: "true"**
7. **Update the **$undefined** (Object):
      - Change [AuctionUI.AggregatedInventory_Brand] to: "$Brand"**
8. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
