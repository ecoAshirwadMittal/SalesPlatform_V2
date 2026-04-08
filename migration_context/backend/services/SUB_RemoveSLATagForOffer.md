# Microflow Detailed Specification: SUB_RemoveSLATagForOffer

### 📥 Inputs (Parameters)
- **$Offer** (Type: EcoATM_PWS.Offer)

### ⚙️ Execution Flow (Logic Steps)
1. **Call Microflow **Custom_Logging.SUB_Log_Info****
2. **Update **$Offer** (and Save to DB)
      - Set **OfferBeyondSLA** = `false`**
3. **Show Message (Information): `SLA tags has been removed for this offer!`**
4. **Call Microflow **Custom_Logging.SUB_Log_Info****
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.