# Java Action: DeepClone

> Clones objects, their associations and even referred objects. - Source: the original object to copy - Target: the object to copy it into (should be of the same type, or a specialization) - MembersToSkip: members which should not be set at all - MembersToKeep: references which should be set, but not cloned. (so source and target will refer to exactly the same object). If an association is not part of this property, it will be cloned. - ReverseAssociations: 1 - 0 assications which refer to target, which will be cloned as well. Only the reference name itself needs to be mentioned. - excludeEntities: entities that will not be cloned. references to these entities will refer to the same object as the source did. - excludeModules: modules that will have none of their enities cloned. Behaves similar to excludeEntities. members format: <membername> or <module.association> or <module.objecttype/membername>, where objecttype is the concrete type of the object being cloned. reverseAssociation: <module.relation> membersToSkip by automatically contains createdDate and changedDate. membersToKeep by automatically contains System.owner and System.changedBy Note that DeepClone does commit all objects, where Clone does not.

**Returns:** `Boolean`

## Parameters

| Name | Type | Required |
|---|---|---|
| `source` | ParameterizedEntity | ✅ |
| `target` | ParameterizedEntity | ✅ |
| `membersToSkip` | String | ✅ |
| `membersToKeep` | String | ✅ |
| `reverseAssociations` | String | ✅ |
| `excludeEntities` | String | ✅ |
| `excludeModules` | String | ✅ |
