# Microflow Analysis: DS_GetCurrentUserBuyerCodes

### Execution Steps:
1. **Run another process: "AuctionUI.ACT_GetCurrentUser"
      - Store the result in a new variable called **$EcoATMDirectUser****
2. **Retrieve
      - Store the result in a new variable called **$CurrentSessionBuyerCodes****
3. **Take the list **$CurrentSessionBuyerCodes**, perform a [Head], and call the result **$CurrentBuyerCode****
4. **Retrieve
      - Store the result in a new variable called **$UserRoleList****
5. **Take the list **$UserRoleList**, perform a [Find] where: { 'Bidder' }, and call the result **$BidderUserRoleExists****
6. **Decision:** "is logged in user a bidder?"
   - If [false] -> Move to: **If CurrentSession has no BuyerCode?**
   - If [true] -> Move to: **Activity**
7. **Decision:** "If CurrentSession has no BuyerCode?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
