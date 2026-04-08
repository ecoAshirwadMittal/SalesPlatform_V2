# Microflow Analysis: ACT_OpenBuyerEditPage

### Requirements (Inputs):
- **$Buyer** (A record of type: EcoATM_BuyerManagement.Buyer)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$BuyerCodeList****
2. **Take the list **$BuyerCodeList**, perform a [Filter] where: { true }, and call the result **$SoftDeleteList****
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Retrieve
      - Store the result in a new variable called **$UserRoleList****
5. **Take the list **$UserRoleList**, perform a [Find] where: { 'Compliance' }, and call the result **$ComplianceRole****
6. **Take the list **$UserRoleList**, perform a [Find] where: { 'Administrator' }, and call the result **$AdministratorRole****
7. **Decision:** "Compliance Role Empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
8. **Show Page**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
