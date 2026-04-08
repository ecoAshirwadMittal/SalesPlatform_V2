# Snippet: UnitTestOverview

## Widget Tree

- ⚡ **Button**: Run all module tests [Style: Primary] [Style: `margin-right: 10px;`]
  ↳ [acti] → **Microflow**: `UnitTesting.IVK_RunAllTests`
- ⚡ **Button**: Reset all tests [Style: Default] [Class: `btn-bordered btn-primary` | Style: `margin-right: 10px;`]
  ↳ [acti] → **Microflow**: `UnitTesting.IVK_RemoveAllAndFindAllTests`
  ↳ [acti] → **Microflow**: `UnitTesting.IVK_RefreshUnitTestOverview`
- 📦 **ListView** [Context] [Style: `margin-top:10px;`]
- 📦 **DataView** [Context]
  - ⚡ **Button**: Run module tests [Style: Primary] [Class: `btn-primary`]
    ↳ [acti] → **Microflow**: `UnitTesting.StartUnittestRun`
  - ⚡ **Button**: Reset tests [Style: Default] [Style: `margin-left:10px`]
    ↳ [acti] → **Microflow**: `UnitTesting.IVK_RemoveTestSuiteAndFindAllTests`
  - 📝 **CheckBox**: checkBox1 [Style: `display:inline-block;`]
    ↳ [Change] → **Microflow**: `UnitTesting.OCh_TestSuiteRollback`
  - 📦 **ListView** [Association: undefined]
