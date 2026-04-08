# Microflow Analysis: BCo_Token

### Requirements (Inputs):
- **$Token** (A record of type: MxModelReflection.Token)

### Execution Steps:
1. **Update the **$undefined** (Object):
      - Change [MxModelReflection.Token.CombinedToken] to: "$Token/Prefix + $Token/Token + $Token/Suffix"
      - Change [MxModelReflection.Token.Description] to: "if $Token/Description = empty or $Token/Description = ''
then $Token/Token
else $Token/Description"**
2. **Retrieve
      - Store the result in a new variable called **$MxObjectTypeStart****
3. **Decision:** "Token type"
   - If [Attribute] -> Move to: **Retrieve the selected member**
   - If [(empty)] -> Move to: **Set invalid**
   - If [Reference] -> Move to: **Retrieve the selected reference**
4. **Retrieve
      - Store the result in a new variable called **$Member****
5. **Decision:** "has all associations"
   - If [true] -> Move to: **Set the path and status**
   - If [false] -> Move to: **Set invalid**
6. **Update the **$undefined** (Object):
      - Change [MxModelReflection.Token.Status] to: "MxModelReflection.Status.Valid"
      - Change [MxModelReflection.Token.MetaModelPath] to: "$MxObjectTypeStart/CompleteName + '/' + $Member/AttributeName"**
7. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
