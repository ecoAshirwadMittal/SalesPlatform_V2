# Microflow Analysis: ACT_WeeklyPODELETE

### Execution Steps:
1. **Search the Database for **EcoATM_PO.PODetail** using filter: { [ProductID=16687 and EcoATM_PO.PODetail_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code='ADPO']
 } (Call this list **$PODetailList_2**)**
2. **Search the Database for **EcoATM_PO.PODetail** using filter: { [ProductID=16687 and EcoATM_PO.PODetail_BuyerCode/EcoATM_BuyerManagement.BuyerCode/Code='ADPO']
 } (Call this list **$PODetailList_2_1**)**
3. **Retrieve
      - Store the result in a new variable called **$WeeklyPOList****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
