# Microflow Analysis: ACT_ManageFileDocument_ImportOrderStatus

### Requirements (Inputs):
- **$ManageFileDocument** (A record of type: EcoATM_PWS.ManageFileDocument)

### Execution Steps:
1. **Decision:** "exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
2. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
3. **Java Action Call
      - Store the result in a new variable called **$JSONContent****
4. **Import Xml** ⚠️ *(This step has a safety catch if it fails)*
5. **Search the Database for **EcoATM_PWS.OrderStatus** using filter: { Show everything } (Call this list **$ExistingOrderStatusList**)**
6. **Create List
      - Store the result in a new variable called **$FinalOrderStatusList****
7. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
8. **Permanently save **$undefined** to the database.**
9. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
10. **Close Form**
11. **Show Message**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
