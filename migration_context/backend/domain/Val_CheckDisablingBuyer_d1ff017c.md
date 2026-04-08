# Microflow Analysis: Val_CheckDisablingBuyer

### Requirements (Inputs):
- **$Buyer** (A record of type: EcoATM_BuyerManagement.Buyer)

### Execution Steps:
1. **Decision:** "Buyer Disabled?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
2. **Search the Database for **EcoATM_UserManagement.EcoATMDirectUser** using filter: { [(EcoATM_UserManagement.EcoATMDirectUser_Buyer= $Buyer)]
[UserStatus = 'Active'] } (Call this list **$BuyerAssignmentList**)**
3. **Aggregate List
      - Store the result in a new variable called **$BuyerAssignmentCount****
4. **Decision:** "Assigned?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Update the **$undefined** (Object):
      - Change [EcoATM_BuyerManagement.Buyer.isFailedBuyerDisable] to: "false"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
