# Microflow Analysis: ACT_Group_Delete

### Requirements (Inputs):
- **$Group** (A record of type: MicrosoftGraph.Group)

### Execution Steps:
1. **Run another process: "MicrosoftGraph.SUB_Authorization_GetActive"
      - Store the result in a new variable called **$Authorization****
2. **Run another process: "MicrosoftGraph.SUB_Group_Delete"
      - Store the result in a new variable called **$Deleted****
3. **Decision:** "Check if "$Deleted" is true"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Close Form**
5. **Delete**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
