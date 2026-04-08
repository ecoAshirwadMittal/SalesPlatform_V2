# Microflow Analysis: ACT_ConnectionDetails_ValidateConnectionWithJWT

### Requirements (Inputs):
- **$ConnectionDetails** (A record of type: SnowflakeRESTSQL.ConnectionDetails)

### Execution Steps:
1. **Create Object
      - Store the result in a new variable called **$NewStatement****
2. **Run another process: "SnowflakeRESTSQL.ConnectionDetails_GenerateToken_JWT"
      - Store the result in a new variable called **$Token****
3. **Run another process: "SnowflakeRESTSQL.POST_v1_ExecuteStatement"
      - Store the result in a new variable called **$HttpResponseList****
4. **Decision:** "Response available?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
5. **Show Message**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
