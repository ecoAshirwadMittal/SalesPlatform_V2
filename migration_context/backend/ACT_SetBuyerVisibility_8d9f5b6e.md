# Microflow Analysis: ACT_SetBuyerVisibility

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$UserRoleList****
2. **Search the Database for **System.UserRole** using filter: { [(Name = 'Bidder')] } (Call this list **$BidderUserRole**)**
3. **Take the list **$UserRoleList**, perform a [Contains], and call the result **$HasBidderRole****
4. **Decision:** "Has Bidder Role?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Update the **$undefined** (Object):
      - Change [EcoATM_UserManagement.EcoATMDirectUser.IsBuyerRole] to: "true"**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
