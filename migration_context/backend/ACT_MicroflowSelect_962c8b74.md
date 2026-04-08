# Microflow Analysis: ACT_MicroflowSelect

### Requirements (Inputs):
- **$Microflow** (A record of type: DeepLink.Microflow)
- **$DeepLink** (A record of type: DeepLink.DeepLink)

### Execution Steps:
1. **Run another process: "DeepLink.UpdateMicroflowMetaData"**
2. **Decision:** "Check condition"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Update the **$undefined** (Object):
      - Change [DeepLink.DeepLink.UseStringArgument] to: "false"**
4. **Decision:** "string argument"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Sub microflow**
5. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName****
6. **Aggregate List
      - Store the result in a new variable called **$Count****
7. **Decision:** "Check condition"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
8. **Take the list **$ReturnValueName**, perform a [Head], and call the result **$ObjectParam****
9. **Update the **$undefined** (Object):
      - Change [DeepLink.DeepLink.ObjectType] to: "$ObjectParam/Key"**
10. **Update the **$undefined** (Object):**
11. **Close Form**
12. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
