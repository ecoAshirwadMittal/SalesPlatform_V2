# Microflow Analysis: SUB_BidData_Transform

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)
- **$ExcelIMport_BidData** (A record of type: Custom_Excel_Import.BidDataExport)
- **$NP_BuyerCodeSelect_Helper** (A record of type: EcoATM_BuyerManagement.NP_BuyerCodeSelect_Helper)
- **$ImportFile** (A record of type: Custom_Excel_Import.ImportFile)

### Execution Steps:
1. **Log Message**
2. **Create Variable**
3. **Retrieve
      - Store the result in a new variable called **$ScheduleAuction****
4. **Retrieve
      - Store the result in a new variable called **$Auction****
5. **Search the Database for **AuctionUI.SchedulingAuction** using filter: { [AuctionUI.SchedulingAuction_Auction = $Auction and Round = $ScheduleAuction/Round -1] } (Call this list **$ScheduleAuction_PriorRound**)**
6. **Create Variable**
7. **Decision:** "prior exists?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
8. **Run another process: "EcoATM_BuyerManagement.SUB_IsSpecialTreatmentBuyer"
      - Store the result in a new variable called **$isSpecialTreatmentBuyer****
9. **Change Variable**
10. **Change Variable**
11. **Retrieve
      - Store the result in a new variable called **$Week****
12. **Search the Database for **AuctionUI.BidData** using filter: { 
[AuctionUI.BidData_BidRound = $BidRound] } (Call this list **$Existing_BidDataList**)**
13. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction = $ScheduleAuction_PriorRound
and AuctionUI.BidData_BuyerCode = $BidRound/AuctionUI.BidRound_BuyerCode]
 } (Call this list **$Existing_BidDataList_PriorRound**)**
14. **Search the Database for **AuctionUI.BidData** using filter: { [Code = $NP_BuyerCodeSelect_Helper/Code]
[AuctionUI.BidData_BidRound = $BidRound] } (Call this list **$Existing_BidDataList_1**)**
15. **Create List
      - Store the result in a new variable called **$BidDataList_Updates****
16. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
17. **Decision:** "Sucess?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
18. **Search the Database for **AuctionUI.BidData** using filter: { 
[AuctionUI.BidData_BidRound = $BidRound
and  BidAmount != 0 and BidAmount != empty] } (Call this list **$NotEmpty_Existing_BidData**)**
19. **Decision:** "Round 1?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **NotEmpty_Existing_BidData**
20. **Delete**
21. **Log Message**
22. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
