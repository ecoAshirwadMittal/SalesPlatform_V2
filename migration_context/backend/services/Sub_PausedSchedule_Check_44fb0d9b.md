# Microflow Analysis: Sub_PausedSchedule_Check

### Execution Steps:
1. **Run another process: "TaskQueueScheduler.DS_PausedSchedule_Get"
      - Store the result in a new variable called **$PausedSchedule****
2. **Decision:** "found?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Run another process: "TaskQueueScheduler.Sub_PreviousInstance_Check"
      - Store the result in a new variable called **$StillRunning****
4. **Decision:** "Paused?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
