# Microflow Analysis: BCo_MxObjectReference

### Requirements (Inputs):
- **$MxObjectReference** (A record of type: MxModelReflection.MxObjectReference)

### Execution Steps:
1. **Decision:** "has readable name?"
   - If [false] -> Move to: **Set the readable name**
   - If [true] -> Move to: **Finish**
2. **Update the **$undefined** (Object):
      - Change [MxModelReflection.MxObjectReference.ReadableName] to: "$MxObjectReference/CompleteName"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
