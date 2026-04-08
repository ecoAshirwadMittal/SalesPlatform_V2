# Microflow Analysis: BCo_Deeplink_SetAttributes

### Requirements (Inputs):
- **$DeepLink** (A record of type: DeepLink.DeepLink)

### Execution Steps:
1. **Retrieve
      - Store the result in a new variable called **$microflow****
2. **Retrieve
      - Store the result in a new variable called **$entity****
3. **Retrieve
      - Store the result in a new variable called **$attribute****
4. **Update the **$undefined** (Object):
      - Change [DeepLink.DeepLink.Microflow] to: "if $microflow != empty then
	$microflow/Name
else
	$DeepLink/Microflow
"
      - Change [DeepLink.DeepLink.ObjectType] to: "if $entity != empty then
	$entity/Name
else
	$DeepLink/ObjectType"
      - Change [DeepLink.DeepLink.ObjectAttribute] to: "if $attribute != empty then
	$attribute/Name
else
	$DeepLink/ObjectAttribute"**
5. **Process successfully completed.**

**Conclusion:** This process sends back a [Boolean] result.
