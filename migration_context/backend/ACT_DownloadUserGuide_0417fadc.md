# Microflow Analysis: ACT_DownloadUserGuide

### Execution Steps:
1. **Search the Database for **EcoATM_MDM.UserHelperGuide** using filter: { [Active]
[GuideType='Auctions']
 } (Call this list **$UserHelperGuide**)**
2. **Decision:** "Check condition"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Download File**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
