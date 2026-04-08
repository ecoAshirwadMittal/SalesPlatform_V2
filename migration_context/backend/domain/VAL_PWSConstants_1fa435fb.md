# Microflow Analysis: VAL_PWSConstants

### Requirements (Inputs):
- **$PWSConstants** (A record of type: EcoATM_PWS.PWSConstants)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "SLA Days valid?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Decision:** "valid sales email?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Decision:** "first reminder?"
   - If [true] -> Move to: **1st reminder hours not empty?**
   - If [false] -> Move to: **Finish**
5. **Decision:** "1st reminder hours not empty?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
6. **Decision:** "second reminder?"
   - If [true] -> Move to: **2nd reminder hours not empty?**
   - If [false] -> Move to: **Finish**
7. **Decision:** "2nd reminder hours not empty?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
8. **Decision:** "both reminders enabled?"
   - If [true] -> Move to: **hours 2 > hours 1**
   - If [false] -> Move to: **Finish**
9. **Decision:** "hours 2 > hours 1"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
10. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
