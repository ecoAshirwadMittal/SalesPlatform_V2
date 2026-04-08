# Microflow Analysis: IVK_PerformTests

### Execution Steps:
1. **Run another process: "OQL.IVK_CreateContent"
      - Store the result in a new variable called **$Variable****
2. **Search the Database for **OQL.ExamplePerson** using filter: { [Name != empty]
[OQL.MarriedTo/OQL.ExamplePerson] } (Call this list **$ExamplePerson**)**
3. **Java Action Call
      - Store the result in a new variable called **$DateOfBirth****
4. **Java Action Call
      - Store the result in a new variable called **$Number****
5. **Java Action Call
      - Store the result in a new variable called **$Name****
6. **Java Action Call
      - Store the result in a new variable called **$Age****
7. **Java Action Call
      - Store the result in a new variable called **$LongAge****
8. **Java Action Call
      - Store the result in a new variable called **$HeightInDecimal****
9. **Java Action Call
      - Store the result in a new variable called **$Active****
10. **Java Action Call
      - Store the result in a new variable called **$Gender****
11. **Retrieve
      - Store the result in a new variable called **$Marriedto****
12. **Java Action Call
      - Store the result in a new variable called **$ReturnValueName****
13. **Java Action Call
      - Store the result in a new variable called **$Results****
14. **Take the list **$Results**, perform a [Head], and call the result **$ExamplePersonResult****
15. **Decision:** "Same person found?"
   - If [true] -> Move to: **Activity**
   - If [false] -> Move to: **Activity**
16. **Run another process: "OQL.IVK_TestCount"**
17. **Show Message**
18. **Process successfully completed.**

**Conclusion:** This process sends back a [Void] result.
