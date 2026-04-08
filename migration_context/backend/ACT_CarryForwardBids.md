# Microflow Detailed Specification: ACT_CarryForwardBids

### 📥 Inputs (Parameters)
- **$CarryOverBidsNP** (Type: AuctionUI.CarryOverBidsNP)

### ⚙️ Execution Flow (Logic Steps)
1. **Create Variable **$TimerName** = `'CarryOverBidsJavaAction'`**
2. **Create Variable **$Description** = `'CarryOverBids JA: From Buyercode '+$CarryOverBidsNP/FromBuyerCode+' Week '+$CarryOverBidsNP/FromWeek+' '+$CarryOverBidsNP/FromYear+' To BuyerCode '+$CarryOverBidsNP/ToBuyerCode+' Week '+$CarryOverBidsNP/ToWeek+' '+$CarryOverBidsNP/ToYear`**
3. **Call Microflow **Custom_Logging.SUB_Log_StartTimer****
4. **JavaCallAction**
5. **Call Microflow **Custom_Logging.SUB_Log_EndTimer****
6. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.