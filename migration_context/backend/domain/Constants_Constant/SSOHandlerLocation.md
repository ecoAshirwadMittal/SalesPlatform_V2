# Constant: SSOHandlerLocation

> When a deeplink configuration allows anonymous users the SSO Handler will be requested before redirecting the user to its destination. However, the SSO Handler will only be requested when the user session is an anonymous user session. Useful in situations when SSO handler does not prompt users for authentication, allowing anonymous users. The orginial deeplink location will be appended to the login location when the login location ends with a '='. Example, in case of MendixSSO: '/openid/login?continuation='

| Property | Value |
|---|---|
| **Type** | `String` |
| **Default Value** | `(empty)` |
| **Exposed to Client** | ❌ |
