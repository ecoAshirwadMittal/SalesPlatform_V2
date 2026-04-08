# Microflow Analysis: ACT_ShowBuyerCodeSelectPopup

### Requirements (Inputs):
- **$Parent_NPBuyerCodeSelectHelper** (A record of type: EcoATM_BuyerManagement.Parent_NPBuyerCodeSelectHelper)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Retrieve
      - Store the result in a new variable called **$UserRoleList****
3. **Take the list **$UserRoleList**, perform a [Filter] where: { 'Bidder' }, and call the result **$BidderUserRole****
4. **Decision:** "exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
5. **Run another process: "Custom_Logging.SUB_Log_Info"**
6. **Show Page**
7. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
8. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
