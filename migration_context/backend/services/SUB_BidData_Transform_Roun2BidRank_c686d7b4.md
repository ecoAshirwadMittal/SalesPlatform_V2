# Microflow Analysis: SUB_BidData_Transform_Roun2BidRank

### Requirements (Inputs):
- **$BidRound** (A record of type: AuctionUI.BidRound)
- **$ExcelIMport_BidData** (A record of type: Custom_Excel_Import.BidDataRankRound2Export)
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
10. **Retrieve
      - Store the result in a new variable called **$Week****
11. **Search the Database for **AuctionUI.BidData** using filter: { 
[AuctionUI.BidData_BidRound = $BidRound] } (Call this list **$Existing_BidDataList**)**
12. **Search the Database for **AuctionUI.BidData** using filter: { [AuctionUI.BidData_BidRound/AuctionUI.BidRound/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction = $ScheduleAuction_PriorRound
and AuctionUI.BidData_BuyerCode = $BidRound/AuctionUI.BidRound_BuyerCode]
 } (Call this list **$Existing_BidDataList_PriorRound**)**
13. **Create List
      - Store the result in a new variable called **$BidDataList_Updates****
14. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
15. **Decision:** "Sucess?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
16. **Search the Database for **AuctionUI.BidData** using filter: { 
[AuctionUI.BidData_BidRound = $BidRound
and  BidAmount != 0 and BidAmount != empty] } (Call this list **$NotEmpty_Existing_BidData**)**
17. **Decision:** "Round 1?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **NotEmpty_Existing_BidData**
18. **Delete**
19. **Log Message**
20. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
