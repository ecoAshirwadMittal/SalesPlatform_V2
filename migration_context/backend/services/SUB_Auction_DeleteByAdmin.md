# Microflow Detailed Specification: SUB_Auction_DeleteByAdmin

### 📥 Inputs (Parameters)
- **$Auction** (Type: AuctionUI.Auction)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
2. **Retrieve related **Auction_Week** via Association from **$Auction** (Result: **$Week**)**
3. **JavaCallAction**
4. **Call Microflow **Custom_Logging.SUB_Log_Info****
5. **Delete**
6. **Call Microflow **Custom_Logging.SUB_Log_Info****
7. **ExecuteDatabaseQuery**
8. **Call Microflow **Custom_Logging.SUB_Log_Info****
9. **ExecuteDatabaseQuery**
10. **Call Microflow **Custom_Logging.SUB_Log_Info****
11. **ExecuteDatabaseQuery**
12. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
13. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.