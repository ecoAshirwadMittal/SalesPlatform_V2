# Import Mapping: IM_EcoATMSetting

**JSON Structure:** `Custom_Logging.JSON_EcoATMSetting`

## Mapping Structure

- **Root** (Object) → `Custom_Logging.EcoATMSetting`
  - **Configuration** (Object) → `Custom_Logging.Configuration`
    - **Filename** (Value)
      - Attribute: `Custom_Logging.Configuration.Filename`
    - **IsPerEnv** (Value)
      - Attribute: `Custom_Logging.Configuration.IsPerEnv`
    - **Microflow** (Value)
      - Attribute: `Custom_Logging.Configuration.Microflow`
  - **Environment** (Object) → `Custom_Logging.Environment`
    - **Name** (Value)
      - Attribute: `Custom_Logging.Environment.Name`
    - **Comment** (Value)
      - Attribute: `Custom_Logging.Environment.Comment`
    - **Url** (Value)
      - Attribute: `Custom_Logging.Environment.Url`
    - **IsDefault** (Value)
      - Attribute: `Custom_Logging.Environment.IsDefault`
