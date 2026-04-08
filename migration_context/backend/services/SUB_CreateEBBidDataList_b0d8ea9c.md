# Microflow Analysis: SUB_CreateEBBidDataList

### Requirements (Inputs):
- **$EBBuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$Week** (A record of type: EcoATM_MDM.Week)

### Execution Steps:
1. **Create List
      - Store the result in a new variable called **$EBBidDataList****
2. **Search the Database for **EcoATM_DA.DeviceBuyer** using filter: { [EcoATM_DA.DeviceBuyer_DAWeek = $Week/EcoATM_DA.DAWeek_Week]
[IsChanged]
[EB]
 } (Call this list **$DeviceBuyerList**)**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
