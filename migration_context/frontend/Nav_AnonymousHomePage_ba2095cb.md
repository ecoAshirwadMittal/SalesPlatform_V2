# Microflow Analysis: Nav_AnonymousHomePage

### Execution Steps:
1. **Run another process: "DeepLink.DeepLinkHome"
      - Store the result in a new variable called **$DeeplinkExecuted****
2. **Run another process: "Custom_Logging.SUB_Log_Info"**
3. **Decision:** "Deeplink executed?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
