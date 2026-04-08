# Microflow Analysis: ACT_GenerateDeposcoV2Token

### Requirements (Inputs):
- **$GenerateNewToken** (A record of type: Object)

### Execution Steps:
1. **Create Variable**
2. **Search the Database for **EcoATM_PWSIntegration.AccessToken** using filter: { [createdDate>=$CurrentTime]
 } (Call this list **$AccessToken**)**
3. **Decision:** "Is GenerateNewToken?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
4. **Delete**
5. **Java Action Call
      - Store the result in a new variable called **$EncodedAuth****
6. **Rest Call**
7. **Import Xml**
8. **Create Object
      - Store the result in a new variable called **$NewAccessToken****
9. **Process successfully completed.**

**Conclusion:** This process sends back a [String] result.
