# Microflow Analysis: ACT_UpdatePropertiesForDevices

### Requirements (Inputs):
- **$PropertiesUtility** (A record of type: EcoATM_PWSMDM.PropertiesUtility)

### Execution Steps:
1. **Decision:** "UpdateBrand?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
2. **Decision:** "UpdateCapacity?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
3. **Decision:** "UpdateCarrier?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
4. **Decision:** "UpdateCategory?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
5. **Decision:** "UpdateColor?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
6. **Decision:** "UpdateGrade?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
7. **Decision:** "UpdateModel?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Activity**
8. **Show Message**
9. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
