# Nanoflow: ACT_EmailMessage_Discard

**Allowed Roles:** Email_Connector.EmailConnectorAdmin

## 📥 Inputs

- **$EmailMessage** (Email_Connector.EmailMessage)

## ⚙️ Execution Flow

1. **Retrieve related **EmailMessage_EmailAccount** via Association from **$EmailMessage** (Result: **$EmailAccount**)**
2. **Update **$EmailMessage**
      - Set **Content** = `empty`**
3. **Update **$EmailAccount**
      - Set **ComposeEmail** = `false`**
4. 🏁 **END:** Return empty

## 🏁 Returns
`Void`
