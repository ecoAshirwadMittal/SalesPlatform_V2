# Microflow Detailed Specification: SUB_Authorization_ProcessSuccessfulResponse

### 📥 Inputs (Parameters)
- **$Response** (Type: System.HttpResponse)
- **$Authorization** (Type: MicrosoftGraph.Authorization)

### ⚙️ Execution Flow (Logic Steps)
1. **ImportXml**
2. **JavaCallAction**
3. **Update **$Authorization** (and Save to DB)
      - Set **Successful** = `true`**
4. **LogMessage**
5. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.