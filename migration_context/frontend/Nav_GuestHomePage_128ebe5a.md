# Microflow Analysis: Nav_GuestHomePage

### Execution Steps:
1. **Run another process: "DeepLink.DeepLinkHome"
      - Store the result in a new variable called **$DeeplinkExecuted****
2. **Decision:** "Deeplink executed?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
