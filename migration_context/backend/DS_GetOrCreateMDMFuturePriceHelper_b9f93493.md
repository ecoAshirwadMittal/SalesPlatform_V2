# Microflow Analysis: DS_GetOrCreateMDMFuturePriceHelper

### Execution Steps:
1. **Search the Database for **EcoATM_PWS.MDMFuturePriceHelper** using filter: { Show everything } (Call this list **$MDMFuturePriceHelperList**)**
2. **Decision:** "MDMFuturePriceHelper List Exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Take the list **$MDMFuturePriceHelperList**, perform a [Head], and call the result **$MDMFuturePriceHelper****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
