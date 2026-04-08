# Microflow Analysis: ACT_FeatureFlag_RetrieveOrCreate

### Requirements (Inputs):
- **$FeatureFlag_String** (A record of type: Object)

### Execution Steps:
1. **Search the Database for **Eco_Core.PWSFeatureFlag** using filter: { [
  (
    Name = $FeatureFlag_String
  )
] } (Call this list **$FeatureFlag**)**
2. **Decision:** "Has Feature flag?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
3. **Create Object
      - Store the result in a new variable called **$NewFeatureFlag****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
