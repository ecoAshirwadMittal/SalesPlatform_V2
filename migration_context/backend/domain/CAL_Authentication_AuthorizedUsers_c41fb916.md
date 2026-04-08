# Microflow Analysis: CAL_Authentication_AuthorizedUsers

### Requirements (Inputs):
- **$Authentication** (A record of type: MicrosoftGraph.Authentication)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$AuthorizationList****
2. **Take the list **$AuthorizationList**, perform a [Filter] where: { true }, and call the result **$NewAuthorizationList****
3. **Aggregate List
      - Store the result in a new variable called **$Count****
4. **Process successfully completed.**

**Conclusion:** This process sends back a [Integer] result.
