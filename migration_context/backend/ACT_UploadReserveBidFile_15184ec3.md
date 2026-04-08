# Microflow Analysis: ACT_UploadReserveBidFile

### Requirements (Inputs):
- **$ReserveBidFile** (A record of type: EcoATM_EB.ReserveBidFile)

### Execution Steps:
1. **Run another process: "AuctionUI.ACT_GET_CurrentUser"
      - Store the result in a new variable called **$EcoATMDirectUser****
2. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
3. **Import Excel Data
      - Store the result in a new variable called **$EB_PricingList****
4. **Search the Database for **EcoATM_EB.ReserveBid** using filter: { Show everything } (Call this list **$ReserveBidList**)**
5. **Create List
      - Store the result in a new variable called **$ReservedBidChanged****
6. **Create List
      - Store the result in a new variable called **$ReservedBidAuditList****
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Permanently save **$undefined** to the database.**
9. **Permanently save **$undefined** to the database.**
10. **Close Form**
11. **Delete**
12. **Export Xml**
13. **Run another process: "EcoATM_EB.SUB_ReserveBidData_UpdateSnowflake"**
14. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
15. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
