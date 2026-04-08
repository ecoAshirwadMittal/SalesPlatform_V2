# Microflow Detailed Specification: BCo_Deeplink_SetAttributes

### 📥 Inputs (Parameters)
- **$DeepLink** (Type: DeepLink.DeepLink)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **DeepLink_Microflow** via Association from **$DeepLink** (Result: **$microflow**)**
2. **Retrieve related **DeepLink_Entity** via Association from **$DeepLink** (Result: **$entity**)**
3. **Retrieve related **DeepLink_Attribute** via Association from **$DeepLink** (Result: **$attribute**)**
4. **Update **$DeepLink**
      - Set **Microflow** = `if $microflow != empty then $microflow/Name else $DeepLink/Microflow`
      - Set **ObjectType** = `if $entity != empty then $entity/Name else $DeepLink/ObjectType`
      - Set **ObjectAttribute** = `if $attribute != empty then $attribute/Name else $DeepLink/ObjectAttribute`**
5. 🏁 **END:** Return `true`

**Final Result:** This process concludes by returning a [Boolean] value.