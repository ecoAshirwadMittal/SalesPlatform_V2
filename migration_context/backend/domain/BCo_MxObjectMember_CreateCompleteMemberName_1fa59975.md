# Microflow Analysis: BCo_MxObjectMember_CreateCompleteMemberName

### Requirements (Inputs):
- **$MxObjectMember** (A record of type: MxModelReflection.MxObjectMember)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$MxObjectType****
2. **Decision:** "Check condition"
   - If [true] -> Move to: **Set the Complete name for the Member**
   - If [false] -> Move to: **Set the Complete name for the Member**
3. **Update the **$undefined** (Object):
      - Change [MxModelReflection.MxObjectMember.CompleteName] to: "$MxObjectType/CompleteName + ' / ' + $MxObjectMember/AttributeName"**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
