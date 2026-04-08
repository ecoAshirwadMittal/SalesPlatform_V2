# Microflow Analysis: SUB_SendSubmitBidConfirmationEmail

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)

### Execution Steps:
1. **Search the Database for **Email_Connector.EmailAccount** using filter: { Show everything } (Call this list **$EmailAccount**)**
2. **Retrieve
      - Store the result in a new variable called **$SchedulingAuction****
3. **Run another process: "AuctionUI.SUB_DateTimeEmailBody"
      - Store the result in a new variable called **$DateTime****
4. **Search the Database for **EcoATM_UserManagement.EcoATMDirectUser** using filter: { [id=$currentUser]
 } (Call this list **$EcoATMDirectUser**)**
5. **Retrieve
      - Store the result in a new variable called **$UserRoleList****
6. **Take the list **$UserRoleList**, perform a [Find] where: { 'Bidder' }, and call the result **$Bidder****
7. **Decision:** "Bidder?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
8. **Create Object
      - Store the result in a new variable called **$LinkObject****
9. **Search the Database for **Email_Connector.EmailTemplate** using filter: { [TemplateName='SubmitBids_Confirmation']
 } (Call this list **$EmailTemplate**)**
10. **Update the **$undefined** (Object):
      - Change [Email_Connector.EmailTemplate.To] to: "$EcoATMDirectUser/Name
"**
11. **Java Action Call
      - Store the result in a new variable called **$Variable****
12. **Delete**
13. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
