# Microflow Detailed Specification: Sub_CreateColumnsFromTemplate

### 📥 Inputs (Parameters)
- **$Template** (Type: ExcelImporter.Template)

### ⚙️ Execution Flow (Logic Steps)
1. **Retrieve related **Template_MxObjectType** via Association from **$Template** (Result: **$MxObjectType**)**
2. **Create Variable **$AutoNumberToBeIgnored** = `MxModelReflection.PrimitiveTypes.AutoNumber`**
3. **DB Retrieve **MxModelReflection.MxObjectMember** Filter: `[MxModelReflection.MxObjectMember_MxObjectType = $MxObjectType][AttributeTypeEnum != $AutoNumberToBeIgnored]` (Result: **$MemberList**)**
4. **Retrieve related **Column_Template** via Association from **$Template** (Result: **$ColumnList**)**
5. **AggregateList**
6. 🔄 **LOOP:** For each **$Member** in **$MemberList**
   │ 1. **Create Variable **$ColumnTitle** = `$Member/AttributeName`**
   │ 2. **List Operation: **Find** on **$undefined** where `$ColumnTitle` (Result: **$Column**)**
   │ 3. 🔀 **DECISION:** `$Column != empty`
   │    ➔ **If [true]:**
   │       1. **Update **$Column**
      - Set **MappingType** = `ExcelImporter.MappingType.Attribute`
      - Set **FindAttribute** = `$Member/AttributeName`
      - Set **Column_MxObjectMember** = `$Member`
      - Set **Column_MxObjectType** = `$MxObjectType`**
   │    ➔ **If [false]:**
   │       1. **Update Variable **$NextColNumber** = `if $NextColNumber = empty then 0 else $NextColNumber + 1`**
   │       2. **Create **ExcelImporter.Column** (Result: **$NewColumn**)
      - Set **ColNumber** = `$NextColNumber`
      - Set **Text** = `$ColumnTitle`
      - Set **Column_Template** = `$Template`**
   │       3. **Add **$$NewColumn** to/from list **$ColumnList****
   │          *(Merging with existing path logic)*
   └─ **End Loop**
7. **Commit/Save **$ColumnList** to Database**
8. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.