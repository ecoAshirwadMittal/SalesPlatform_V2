# Microflow Analysis: Sub_MapProcessQueueToSchedule

### Requirements (Inputs):
- **$Process** (A record of type: ProcessQueue.Process)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$SharedQueueConfiguration****
2. **Create Variable**
3. **Change Variable**
4. **Search the Database for **MicroflowScheduler.Schedule** using filter: { [MicroflowName=$Process/MicroflowFullname or OldMicroflowName=$Process/MicroflowFullname]
[QueueName=$QueueName] } (Call this list **$Schedule**)**
5. **Decision:** "Exists?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
6. **Create Object
      - Store the result in a new variable called **$NewSchedule****
7. **Run another process: "MicroflowScheduler.Sub_UPDSchedule"**
8. **Decision:** "Valid?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Object] result.
