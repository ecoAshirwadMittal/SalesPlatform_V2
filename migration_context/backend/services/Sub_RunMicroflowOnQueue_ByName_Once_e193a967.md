# Microflow Analysis: Sub_RunMicroflowOnQueue_ByName_Once

### Requirements (Inputs):
- **$MicroflowName** (A record of type: Object)

### Execution Steps:
1. **Run another process: "TaskQueueScheduler.Sub_Schedule_FetchByName"
      - Store the result in a new variable called **$Schedule****
2. **Decision:** "empty?"
   - If [false] -> Move to: **Running already?**
   - If [true] -> Move to: **Finish**
3. **Decision:** "Running already?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
4. **Run another process: "TaskQueueScheduler.ACT_Schedule_RunMicroflow"
      - Store the result in a new variable called **$Schedule_1****
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
