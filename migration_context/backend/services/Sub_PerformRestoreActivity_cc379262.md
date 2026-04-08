# Microflow Analysis: Sub_PerformRestoreActivity

### Requirements (Inputs):
- **$AppUrl** (A record of type: Object)

### Execution Steps:
1. **Run another process: "Custom_Logging.SUB_Log_StartTimer"**
2. **Search the Database for **EcoATM_UserManagement.EcoATMDirectUser** using filter: { [not (contains(Email,'ecoatm'))] } (Call this list **$NonEcoATMUsers**)**
3. **Delete**
4. **Search the Database for **SAML20.SSOConfiguration** using filter: { [Active] } (Call this list **$CurrentActiveSaml**)**
5. **Update the **$undefined** (Object):
      - Change [SAML20.SSOConfiguration.Active] to: "false"
      - **Save:** This change will be saved to the database immediately.**
6. **Search the Database for **SAML20.SSOConfiguration** using filter: { [Alias='QA Config'] } (Call this list **$RequiredSAMLForEnvironment**)**
7. **Decision:** "$RequiredSAMLForEnvironment!=empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
8. **Update the **$undefined** (Object):
      - Change [SAML20.SSOConfiguration.Active] to: "true"
      - **Save:** This change will be saved to the database immediately.**
9. **Create List
      - Store the result in a new variable called **$EcoATMDirectUserList****
10. **Create List
      - Store the result in a new variable called **$BuyerList****
11. **Create List
      - Store the result in a new variable called **$BuyerCodeList****
12. **Create Variable**
13. **Search the Database for **System.UserRole** using filter: { [Name='Bidder'] } (Call this list **$BidderUserRole**)**
14. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
15. **Permanently save **$undefined** to the database.**
16. **Permanently save **$undefined** to the database.**
17. **Permanently save **$undefined** to the database.**
18. **Search the Database for **TaskQueueScheduler.Schedule** using filter: { [Running] } (Call this list **$RunningSchedules**)**
19. **Loop Start:** Go through every item in **$list** one-by-one:
   > **(End of Loop)**
20. **Permanently save **$undefined** to the database.**
21. **Search the Database for **MicrosoftGraph.Authentication** using filter: { [IsActive] } (Call this list **$ActiveSharepointAuthentication**)**
22. **Update the **$undefined** (Object):
      - Change [MicrosoftGraph.Authentication.IsActive] to: "false"
      - **Save:** This change will be saved to the database immediately.**
23. **Search the Database for **MicrosoftGraph.Authentication** using filter: { [DisplayName='QA'] } (Call this list **$RequiredSharepointAuthentication**)**
24. **Decision:** "$RequiredSharepointAuthentication!=empty?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Finish**
25. **Update the **$undefined** (Object):
      - Change [MicrosoftGraph.Authentication.IsActive] to: "true"
      - **Save:** This change will be saved to the database immediately.**
26. **Run another process: "Custom_Logging.SUB_Log_EndTimer"**
27. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
