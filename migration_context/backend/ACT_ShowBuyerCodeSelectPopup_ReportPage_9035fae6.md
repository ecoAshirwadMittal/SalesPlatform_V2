# Microflow Analysis: ACT_ShowBuyerCodeSelectPopup_ReportPage

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$UserRoleList****
2. **Take the list **$UserRoleList**, perform a [Filter] where: { 'Bidder' }, and call the result **$BidderUserRole****
3. **Decision:** "exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Show Page**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
