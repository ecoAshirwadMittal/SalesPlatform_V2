# Microflow Analysis: ACT_SaveDocument

### Requirements (Inputs):
- **$UserHelperGuide** (A record of type: EcoATM_MDM.UserHelperGuide)

### Execution Steps:
1. **Search the Database for **EcoATM_MDM.UserHelperGuide** using filter: { [GuideType=$UserHelperGuide/GuideType]
[id!=$UserHelperGuide]
[Active=$UserHelperGuide/Active] } (Call this list **$DuplicateGuideDocument**)**
2. **Decision:** "Duplicate guide being created?"
   - If [false] -> Move to: **Activity**
   - If [true] -> Move to: **Activity**
3. **Permanently save **$undefined** to the database.**
4. **Decision:** "Acknowledgement?"
   - If [true] -> Move to: **Finish**
   - If [false] -> Move to: **Activity**
5. **Close Form**
6. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
