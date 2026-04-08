# Microflow Analysis: DS_Attribute

### Requirements (Inputs):
- **$DeepLink** (A record of type: DeepLink.DeepLink)

### Execution Steps:
1. **Search the Database for **DeepLink.Entity** using filter: { [Name = $DeepLink/ObjectType] } (Call this list **$Entity**)**
2. **Retrieve
      - Store the result in a new variable called **$AttributeList****
3. **Process successfully completed.**

**Conclusion:** This process sends back a [List] result.
