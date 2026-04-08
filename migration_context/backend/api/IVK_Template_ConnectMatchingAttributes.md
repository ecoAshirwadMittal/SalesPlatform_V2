# Microflow Detailed Specification: IVK_Template_ConnectMatchingAttributes

### 📥 Inputs (Parameters)
- **$Template** (Type: ExcelImporter.Template)

### ⚙️ Execution Flow (Logic Steps)
1. 🔀 **DECISION:** `$Template/ExcelImporter.Template_MxObjectType != empty`
   ➔ **If [false]:**
      1. **Show Message (Error): `A MetaObject must be selected before a column can be created or changed.`**
      2. 🏁 **END:** Return empty
   ➔ **If [true]:**
      1. **Retrieve related **Template_MxObjectType** via Association from **$Template** (Result: **$MxObjectType**)**
      2. **DB Retrieve **ExcelImporter.Column** Filter: `[ExcelImporter.Column_Template = $Template]` (Result: **$ColumnList**)**
      3. 🔄 **LOOP:** For each **$Column** in **$ColumnList**
         │ 1. **Create Variable **$ColumnTitle** = `if $Column/Text != empty then replaceAll(trim( $Column/Text), ' ', '') else ''`**
         │ 2. **DB Retrieve **MxModelReflection.MxObjectMember** Filter: `[MxModelReflection.MxObjectMember_MxObjectType = $MxObjectType] [MxModelReflection.MxObjectMember_Type/MxModelReflection.ValueType/TypeEnum != 'AutoNumber'] [contains(AttributeName, $ColumnTitle)]` (Result: **$MxObjectMemberList**)**
         │ 3. **AggregateList**
         │ 4. 🔀 **DECISION:** `$count > 1`
         │    ➔ **If [true]:**
         │       1. **Create Variable **$MemberMatched** = `false`**
         │       2. 🔄 **LOOP:** For each **$MxObjectMember** in **$MxObjectMemberList**
         │          │ 1. 🔀 **DECISION:** `trim( toLowerCase( $MxObjectMember/AttributeName ) ) = trim( toLowerCase( $ColumnTitle ) )`
         │          │    ➔ **If [false]:**
         │          │    ➔ **If [true]:**
         │          │       1. **Update **$Column** (and Save to DB)
      - Set **MappingType** = `ExcelImporter.MappingType.Attribute`
      - Set **FindAttribute** = `$MxObjectMember/AttributeName`
      - Set **Column_MxObjectMember** = `$MxObjectMember`**
         │          │       2. **Update Variable **$MemberMatched** = `true`**
         │          └─ **End Loop**
         │       3. 🔀 **DECISION:** `$MemberMatched`
         │          ➔ **If [false]:**
         │             1. 🔄 **LOOP:** For each **$MxObjectMember_1** in **$MxObjectMemberList**
         │                │ 1. **Update **$Column** (and Save to DB)
      - Set **MappingType** = `ExcelImporter.MappingType.Attribute`
      - Set **FindAttribute** = `$MxObjectMember_1/AttributeName`
      - Set **Column_MxObjectMember** = `$MxObjectMember_1`**
         │                └─ **End Loop**
         │          ➔ **If [true]:**
         │    ➔ **If [false]:**
         │       1. 🔄 **LOOP:** For each **$MxObjectMember_1** in **$MxObjectMemberList**
         │          │ 1. **Update **$Column** (and Save to DB)
      - Set **MappingType** = `ExcelImporter.MappingType.Attribute`
      - Set **FindAttribute** = `$MxObjectMember_1/AttributeName`
      - Set **Column_MxObjectMember** = `$MxObjectMember_1`**
         │          └─ **End Loop**
         └─ **End Loop**
      4. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.