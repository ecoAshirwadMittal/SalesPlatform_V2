# Microflow Analysis: SUB_RefreshEBPrice_original

### Execution Steps:
1. **Log Message**
2. **Execute Database Query
      - Store the result in a new variable called **$MaxUploadtime**** ⚠️ *(This step has a safety catch if it fails)*
3. **Take the list **$MaxUploadtime**, perform a [Head], and call the result **$MaxuploadTime****
4. **Decision:** "MaxUploadtime has items?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
5. **Search the Database for **EcoATM_EB.ReserveBidSync** using filter: { Show everything } (Call this list **$ReserveBidSync**)**
6. **Decision:** "Refresh requested?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
7. **Java Action Call**
8. **Decision:** "original path"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
9. **Run another process: "EcoATM_EB.SUB_ReserveBid_DeleteAll"**
10. **Java Action Call
      - Store the result in a new variable called **$duration****
11. **Run another process: "EcoATM_EB.SCH_UpdateEBPrice"**
12. **Decision:** "Check condition"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
13. **Update the **$undefined** (Object):
      - Change [EcoATM_EB.ReserveBidSync.LastSyncDateTime] to: "$MaxuploadTime/MAXUPLOADTIME
"
      - **Save:** This change will be saved to the database immediately.**
14. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
