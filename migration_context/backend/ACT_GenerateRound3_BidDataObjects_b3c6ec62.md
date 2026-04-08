# Microflow Analysis: ACT_GenerateRound3_BidDataObjects

### Requirements (Inputs):
- **$SchedulingAuction** (A record of type: AuctionUI.SchedulingAuction)
- **$Auction** (A record of type: AuctionUI.Auction)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **EcoATM_BuyerManagement.BuyerCode** using filter: { [(Status='Active' 
	and AuctionUI.BidRound_BuyerCode/AuctionUI.BidRound[Submitted=true]/AuctionUI.BidRound_SchedulingAuction/AuctionUI.SchedulingAuction/AuctionUI.SchedulingAuction_Auction = $Auction)

	or (Status='Active' 
		and EcoATM_BuyerManagement.BuyerCode_Buyer/EcoATM_BuyerManagement.Buyer/isSpecialBuyer = true) ]

 } (Call this list **$BuyerCodeList**)**
3. **Create List
      - Store the result in a new variable called **$Round3_BuyerCodes****
4. **Create List
      - Store the result in a new variable called **$BidDataList_ToCommit****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Permanently save **$undefined** to the database.**
7. **Update the **$undefined** (Object):
      - **Save:** This change will be saved to the database immediately.**
8. **Run another process: "AuctionUI.SUB_GenerateRound3_BidDataFiles"** ⚠️ *(This step has a safety catch if it fails)*
9. **Run another process: "AuctionUI.ACT_Round3_StartNotification"** ⚠️ *(This step has a safety catch if it fails)*
10. **Update the **$undefined** (Object):
      - Change [AuctionUI.SchedulingAuction.RoundStatus] to: "AuctionUI.enum_SchedulingAuctionStatus.Started
"
      - Change [AuctionUI.SchedulingAuction_QualifiedBuyers] to: "$Round3_BuyerCodes
"
      - Change [AuctionUI.SchedulingAuction.Round3InitStatus] to: "AuctionUI.Enum_ScheduleAuctionInitStatus.Complete"
      - **Save:** This change will be saved to the database immediately.**
11. **Run another process: "EcoATM_BuyerManagement.Act_GetOrCreateBuyerCodeSubmitConfig"
      - Store the result in a new variable called **$BuyerCodeSubmitConfig****
12. **Decision:** "send auction data to snowflake?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
13. **Run another process: "AuctionUI.SUB_SetAuctionStatus"**
14. **Retrieve
      - Store the result in a new variable called **$Week****
15. **Run another process: "AuctionUI.SUB_SendAuctionAndSchedulingActionToSnowflake_async"**
16. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
17. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
