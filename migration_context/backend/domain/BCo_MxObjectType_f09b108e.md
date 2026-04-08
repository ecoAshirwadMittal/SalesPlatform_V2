# Microflow Analysis: BCo_MxObjectType

### Requirements (Inputs):
- **$MxObjectType** (A record of type: MxModelReflection.MxObjectType)

### Execution Steps:
1. **Decision:** "has readable name?"
   - If [false] -> Move to: **Set the readable name**
   - If [true] -> Move to: **Finish**
2. **Update the **$undefined** (Object):
      - Change [MxModelReflection.MxObjectType.ReadableName] to: "$MxObjectType/Name + ' from the ' + $MxObjectType/Module + ' module'"**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
