# Microflow Analysis: ACT_TestDeposcoAPI

### Requirements (Inputs):
- **$DeposcoConfig** (A record of type: EcoATM_PWSIntegration.DeposcoConfig)

### Execution Steps:
1. **Run another process: "EcoATM_PWSIntegration.SUB_GenerateDeposcoPassword"
      - Store the result in a new variable called **$EncodedAuth****
2. **Create Variable**
3. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
