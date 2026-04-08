# Constant: BindingURI_Redirect

> If you encounter the error (most likely you are using Mac OSX and a Safari browser): “MSIS7046: The SAML protocol parameter ‘RelayState’ was not found or not valid.” Setting this Boolean to true might help you resolve the issue. By default we favour the Post binding as the maximum size exceeds that of a Redirect binding due to it using cookies en post information instead of URL parameters (redirect). The size can be a factor when using encryption.

| Property | Value |
|---|---|
| **Type** | `Boolean` |
| **Default Value** | `False` |
| **Exposed to Client** | ❌ |
