# Microflow Analysis: SUB_Round1BuyerCodes

### Requirements (Inputs):
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer[EcoATM_UserManagement.EcoATMDirectUser_Buyer/EcoATM_UserManagement.EcoATMDirectUser/Name=$currentUser/Name]] } (Call this list **$BuyerCodeList_UserAssigned**)**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
