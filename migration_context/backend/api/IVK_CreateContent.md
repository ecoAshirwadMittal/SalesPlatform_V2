# Microflow Detailed Specification: IVK_CreateContent

### ⚙️ Execution Flow (Logic Steps)
1. **DB Retrieve **OQL.ExamplePerson**  (Result: **$ExamplePersonList**)**
2. **Delete**
3. **Create **OQL.ExamplePerson** (Result: **$NewExample**)
      - Set **Name** = `'John Doe'`
      - Set **DateOfBirth** = `dateTime(1982, 5, 25)`
      - Set **Age** = `34`
      - Set **LongAge** = `34`
      - Set **Active** = `true`
      - Set **HeightInDecimal** = `5.12`
      - Set **Gender** = `OQL.Gender.Male`**
4. **Create **OQL.ExamplePerson** (Result: **$NewExample_1**)
      - Set **Name** = `'Jane Doe'`
      - Set **DateOfBirth** = `dateTime(1975, 7, 12)`
      - Set **Age** = `52`
      - Set **LongAge** = `52`
      - Set **HeightInDecimal** = `4.15`
      - Set **Active** = `true`
      - Set **Gender** = `OQL.Gender.Female`
      - Set **MarriedTo** = `$NewExample`**
5. **Create **OQL.ExamplePerson** (Result: **$NewExample_1_1**)
      - Set **Name** = `'Fred Nurk'`
      - Set **DateOfBirth** = `dateTime(1962, 2, 1)`
      - Set **Age** = `55`
      - Set **LongAge** = `55`
      - Set **HeightInDecimal** = `4.75`
      - Set **Active** = `false`
      - Set **Gender** = `OQL.Gender.Other`**
6. **Create **OQL.ExamplePerson** (Result: **$NewExample_1_1_1**)
      - Set **Name** = `'Joe Bloggs'`
      - Set **DateOfBirth** = `dateTime(2002, 5, 18)`
      - Set **Age** = `17`
      - Set **LongAge** = `17`
      - Set **HeightInDecimal** = `4.90`
      - Set **Active** = `true`
      - Set **Gender** = `OQL.Gender.Male`
      - Set **MarriedTo** = `$NewExample_1_1`**
7. **Create **OQL.ExamplePerson** (Result: **$NewExample_1_1_1_1**)
      - Set **Name** = `'Walter Plinge'`
      - Set **DateOfBirth** = `dateTime(1995, 11, 22)`
      - Set **Age** = `22`
      - Set **LongAge** = `22`
      - Set **HeightInDecimal** = `4.05`
      - Set **Active** = `false`
      - Set **Gender** = `OQL.Gender.Male`**
8. **Create **OQL.ExamplePerson** (Result: **$NewExample_1_1_1_1_1**)**
9. 🏁 **END:** Return empty

**Final Result:** This process concludes by returning a [Void] value.