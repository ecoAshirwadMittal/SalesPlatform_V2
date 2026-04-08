# Microflow Analysis: SUB_CheckForBidder

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$UserRoleList****
2. **Take the list **$UserRoleList**, perform a [Find] where: { 'Bidder' }, and call the result **$Bidder****
3. **Decision:** "bidder found?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
