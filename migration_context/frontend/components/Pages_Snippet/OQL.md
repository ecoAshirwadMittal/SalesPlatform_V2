# Snippet: OQL

> This snippet can be used to: - Test OQL statements before applying them in logic. - Query runtime data and export the result set.

## Widget Tree

- 📦 **DataView** [MF: OQL.DS_GetQuery]
  - ⚡ **Button**: radioButtons1
    ↳ [acti] → **Microflow**: `OQL.ACT_ExecuteOQL`
  - 🧩 **Csv As Table** (ID: `mendix.csvastable.CSVasTable`)
      - csv: [Attr: OQL.Query.CSV]
