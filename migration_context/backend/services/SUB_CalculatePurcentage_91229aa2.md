# Microflow Analysis: SUB_CalculatePurcentage

### Requirements (Inputs):
- **$Numerator** (A record of type: Object)
- **$Divisor** (A record of type: Object)

### Execution Steps:
1. **Decision:** "Divisor > 0"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Finish**
2. **Process successfully completed.**

**Conclusion:** This process sends back a [Decimal] result.
