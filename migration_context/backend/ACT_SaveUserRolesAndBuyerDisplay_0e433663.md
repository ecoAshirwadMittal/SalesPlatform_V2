# Microflow Analysis: ACT_SaveUserRolesAndBuyerDisplay

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerList****
2. **Aggregate List
      - Store the result in a new variable called **$ReducedBuyerList****
3. **Retrieve
      - Store the result in a new variable called **$UserRoleList****
4. **Aggregate List
      - Store the result in a new variable called **$ReduceUserRoleList****
5. **Update the **$undefined** (Object):
      - Change [EcoATM_UserManagement.EcoATMDirectUser.UserBuyerDisplay] to: "$ReducedBuyerList"
      - Change [EcoATM_UserManagement.EcoATMDirectUser.UserRolesDisplay] to: "$ReduceUserRoleList"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
