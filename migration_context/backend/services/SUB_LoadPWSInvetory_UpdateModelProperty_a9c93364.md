# Microflow Analysis: SUB_LoadPWSInvetory_UpdateModelProperty

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_Info"**
2. **Java Action Call
      - Store the result in a new variable called **$NewModelList****
3. **Decision:** "exist?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Create List
      - Store the result in a new variable called **$ModelList****
5. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
6. **Permanently save **$undefined** to the database.**
7. **Run another process: "Custom_Logging.SUB_Log_Info"**
8. **Java Action Call
      - Store the result in a new variable called **$newDevicesSuccess****
9. **Run another process: "Custom_Logging.SUB_Log_Info"**
10. **Java Action Call
      - Store the result in a new variable called **$newDevicesSuccess_1****
11. **Run another process: "Custom_Logging.SUB_Log_Info"**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
