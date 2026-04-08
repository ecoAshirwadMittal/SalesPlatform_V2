# Microflow Analysis: SUB_HasWholesaleBuyerCodesAssigned

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerList****
2. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/EcoATM_UserManagement.EcoATMDirectUser_Buyer/EcoATM_UserManagement.EcoATMDirectUser = $EcoATMDirectUser
and (BuyerCodeType = 'Wholesale' or BuyerCodeType = 'Data_Wipe')] } (Call this list **$BuyerCodeList**)**
3. **Decision:** "? Buyer Code Exists"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
