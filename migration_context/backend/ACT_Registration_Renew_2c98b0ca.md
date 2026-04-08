# Microflow Analysis: ACT_Registration_Renew

### Execution Steps:
1. **Run another process: "DocumentGeneration.SE_AccessToken_Refresh"
      - Store the result in a new variable called **$IsSuccess****
2. **Decision:** "IsSuccess?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Show Message**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
