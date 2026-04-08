# Java Action: SubstituteTemplate

> Given an object and a template, substitutes all fields in the template. Supports attributes, references, referencesets and constants. The general field syntax is '{fieldname}'. Fieldname can be a member of the example object, an attribute which need to be retrieved over an reference(set) or a project constant. All paths are relative from the provided substitute obect. An example template: ------------------ Dear {EmailOrName}, {System.changedBy/FullName} has invited you to join the project {Module.MemberShip_Project/Name}. Sign up is free and can be done here: {@Module.PublicURL}link/Signup ------------------------- useHTMLEncoding identifies whether HTMLEncode is applied to the values before substituting. datetimeformat identifies a format string which is applied to date/time based attributes. Can be left empty. Defaults to "EEE dd MMM yyyy, HH:mm"

**Returns:** `String`

## Parameters

| Name | Type | Required |
|---|---|---|
| `template` | String | ✅ |
| `substitute` | ParameterizedEntity | ✅ |
| `useHTMLEncoding` | Boolean | ✅ |
