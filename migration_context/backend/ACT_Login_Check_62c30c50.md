# Microflow Analysis: ACT_Login_Check

### Requirements (Inputs):
- **$LoginCredentials** (A record of type: AuctionUI.LoginCredentials)

### Execution Steps:
1. **Java Action Call
      - Store the result in a new variable called **$LoginSuccess****
2. **Decision:** "Check condition"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
3. **Show Page**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
