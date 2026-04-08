# Microflow Analysis: SUB_RefreshEBPrice

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
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
7. **Run another process: "EcoATM_EB.SUB_ReserveBid_DeleteAll"**
8. **Run another process: "EcoATM_EB.SCH_UpdateEBPrice"**
9. **Decision:** "Check condition"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
10. **Update the **$undefined** (Object):
      - Change [EcoATM_EB.ReserveBidSync.LastSyncDateTime] to: "$MaxuploadTime/MAXUPLOADTIME
"
      - **Save:** This change will be saved to the database immediately.**
11. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
