# Microflow Analysis: SE_DocumentRequest_Cleanup

### Execution Steps:
1. **Log Message**
2. **Create Variable**
3. **Create Variable**
4. **Create Variable**
5. **Search the Database for **DocumentGeneration.DocumentRequest** using filter: { [ExpirationDate < $CleanupBefore] } (Call this list **$ExpiredDocumentRequestList**)**
6. **Decision:** "Anything
to delete?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
7. **Aggregate List
      - Store the result in a new variable called **$BatchCount****
8. **Change Variable**
9. **Delete**

**Conclusion:** This process sends back a [Void] result.
