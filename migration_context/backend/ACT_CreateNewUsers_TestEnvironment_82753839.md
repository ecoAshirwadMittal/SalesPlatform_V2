# Microflow Analysis: ACT_CreateNewUsers_TestEnvironment

### Requirements (Inputs):
- **$EcoATMDirectUserList** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)
- **$BuyerList** (A record of type: EcoATM_BuyerManagement.Buyer)
- **$BuyerCode** (A record of type: EcoATM_BuyerManagement.BuyerCode)
- **$Email** (A record of type: Object)
- **$UserRole** (A record of type: System.UserRole)
- **$BuyerName** (A record of type: Object)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$HashedPassword****
2. **Create Object
      - Store the result in a new variable called **$NewEcoATMDirectUser****
3. **Create Object
      - Store the result in a new variable called **$NewBuyer****
4. **Create Object
      - Store the result in a new variable called **$NewBuyerCode****
5. **Change List**
6. **Change List**
7. **Change List**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
