# Snippet: RoleDescriptorDetails

## Widget Tree

- 📑 **TabContainer**
  - 📑 **Tab**: "Endpoints"
    - 📦 **ListView** [Association: undefined] [DP: {Style: Striped}]
  - 📑 **Tab**: "Certificates"
    - 📦 **TemplateGrid** [Context]
      - 📦 **DataView** [Context]
  - 📑 **Tab**: "Name ID Format"
    - 📦 **DataGrid** [Context]
      - 📊 **Column**: Name ID Format [Width: 100]
  - 📑 **Tab**: "Attributes"
    - 📑 **TabContainer**
      - 📑 **Tab**: "Direct"
        - 📦 **DataGrid** [Context]
          - 📊 **Column**: Name [Width: 20]
          - 📊 **Column**: Name Format [Width: 20]
          - 📊 **Column**: Friendly name [Width: 20]
          - 📊 **Column**: is required [Width: 20]
          - 📊 **Column**: Consuming Service [Width: 20]
      - 📑 **Tab**: "Attribute Service"
        - 📦 **DataGrid** [Context]
          - 📊 **Column**: Service # [Width: 16]
          - 📊 **Column**: Default Service [Width: 16]
          - 📊 **Column**: Name [Width: 17]
          - 📊 **Column**: Name Format [Width: 17]
          - 📊 **Column**: Friendly name [Width: 17]
          - 📊 **Column**: is required [Width: 17]
