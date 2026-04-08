# Microflow Analysis: VAL_EcoAtmUser

### Requirements (Inputs):
- **$EcoATMDirectUser** (A record of type: EcoATM_UserManagement.EcoATMDirectUser)

### Execution Steps:
1. **Create Variable**
2. **Decision:** "FirstName valid?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
3. **Decision:** "LastName valid?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
4. **Change Variable**
5. **Validation Feedback**
6. **Decision:** "Email valid?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
7. **Change Variable**
8. **Validation Feedback**
9. **Decision:** "User Role valid?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
10. **Change Variable**
11. **Validation Feedback**
12. **Retrieve
      - Store the result in a new variable called **$UserRoleList****
13. **Search the Database for **System.UserRole** using filter: { [(Name = 'Bidder')] } (Call this list **$BidderUserRole**)**
14. **Take the list **$UserRoleList**, perform a [Contains], and call the result **$HasBidderRole****
15. **Decision:** "Has Bidder Role?"
   - If [true] -> Move to: **User Role valid?**
   - If [false] -> Move to: **Finish**
16. **Decision:** "User Role valid?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
17. **Change Variable**
18. **Validation Feedback**
19. **Decision:** "Buyer valid?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Finish**
20. **Change Variable**
21. **Validation Feedback**
22. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
