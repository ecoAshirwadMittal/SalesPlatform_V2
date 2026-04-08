# Microflow Analysis: ACT_FixUpdateDate

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.Order** using filter: { [OracleOrderStatus = 'Success'] } (Call this list **$OrderList**)**
2. **Create List
      - Store the result in a new variable called **$OfferList****
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Permanently save **$undefined** to the database.**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
