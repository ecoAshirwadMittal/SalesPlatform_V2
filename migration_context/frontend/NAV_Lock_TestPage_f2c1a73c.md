# Microflow Analysis: NAV_Lock_TestPage

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.Offer** using filter: { Show everything } (Call this list **$Offer**)**
2. **Java Action Call
      - Store the result in a new variable called **$ObjectInfo****
3. **Decision:** "not lock?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
4. **Show Page**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
