# Microflow Analysis: VAL_Item_IsValid

### Requirements (Inputs):
- **$Item** (A record of type: EcoATM_PWSIntegration.Item)

### Execution Steps:
1. **Decision:** "exist?"
   - If [true] -> Move to: **ItemType**
   - If [false] -> Move to: **Finish**
2. **Decision:** "ItemType"
   - If [true] -> Move to: **SKU ?**
   - If [false] -> Move to: **Finish**
3. **Decision:** "SKU ?"
   - If [true] -> Move to: **Title?**
   - If [false] -> Move to: **Finish**
4. **Decision:** "Title?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
