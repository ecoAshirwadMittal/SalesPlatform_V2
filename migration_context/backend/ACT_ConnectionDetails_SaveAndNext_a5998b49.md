# Microflow Analysis: ACT_ConnectionDetails_SaveAndNext

### Requirements (Inputs):
- **$ConnectionDetails** (A record of type: SnowflakeRESTSQL.ConnectionDetails)

### Execution Steps:
1. **Run another process: "SnowflakeRESTSQL.PrivateKey_RetrieveFromConnectionDetails"
      - Store the result in a new variable called **$PrivateKey****
2. **Run another process: "SnowflakeRESTSQL.ConnectionDetails_Validate"
      - Store the result in a new variable called **$IsValid****
3. **Decision:** "Is valid?"
   - If [false] -> Move to: **Finish**
   - If [true] -> Move to: **Sub microflow**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
