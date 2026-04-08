# Microflow Analysis: Act_SaveSaleRep

### Requirements (Inputs):
- **$SalesRepresentative** (A record of type: EcoATM_BuyerManagement.SalesRepresentative)

### Execution Steps:
1. **Create Variable**
2. **Create Variable**
3. **Search the Database for **EcoATM_BuyerManagement.SalesRepresentative** using filter: { [id !=$SalesRepresentative] } (Call this list **$SalesRepresentativeList**)**
4. **Take the list **$SalesRepresentativeList**, perform a [FindByExpression] where: { toLowerCase($SalesRepFirstName) = toLowerCase($currentObject/SalesRepFirstName) and
toLowerCase($SalesRepLastName) = toLowerCase($currentObject/SalesRepLastName) }, and call the result **$Existing****
5. **Decision:** "SalesRepresentative_Existing exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
6. **Show Message**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
