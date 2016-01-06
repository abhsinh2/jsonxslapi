# Description
Style sheet to convert given JSON into certain output. 

Input data JSON:
```
{
 "hosts": [{
     "zone": "internal",
     "host_name": "machine1",
     "service": "compute"
   },
   {
     "zone": "internal",
     "host_name": "machine2",
     "service": "datastore"
   }]
}
```

Input Json XSL:
```
{
   "rules" : {
       "main": "<Machines>{Machines}</Machines>",
       "Machines": "{hosts}",
       "hosts": {
           "main": "<Machine><name>{$host_name}</name><type>host</type></Machine>"
       }
   }
}
```

Output from JAVA API:
```
<Machines>
    <Machine>
        <name>machine1</name>
        <type>host</type>
    </Machine>
    <Machine>
        <name>machine2</name>
        <type>host</type>
    </Machine>
</Machines>
```

# Approach

## Conventions:
* `{name}` -> variable name which is key in current rule Json Object
* `{$name}` -> variable name which is key in current data Json Object
* `{$$name}` -> variable to passed from Java code to this Json.

## Java API usage:
The Java API will take three arguments, 
1. data is Json on which rules will be applied.
2. rules in Json to be applied on data Json
3. Java Map as input parameters to rules Json (for `{$$name}`).

```
JsonXSLT jsonXSLT = new JsonXSLT(dataJsonElement, rulesJsonElement, parameters);
String result = jsonXSLT.apply();
```

## Approach to apply JSON XSL:

**Reading rules:**
- My Java API will start from rules.main property and will return the output of the property. 
- During traversing rules Json if found:
      - `{$$name}` then Java API will replace with the value passed as Map as input  
        parameters.  
      - `{$name}` then Java API will replace with value of current data Json key “name”
      - `{name}` then Java will replace with value of current rule Json key “name”
      
**Explanation:** 
Let me explain the above example:

```
currRuleInput = {
   "rules" : {
       "main": "<Machines>{Machines}</Machines>",
       "Machines": "{hosts}",
       "hosts": {
           "main": "<Machine><name>{$host_name}</name><type>host</type></Machine>"
       }
   }
};
```

```
currDataInput = {
 "hosts": [{
     "zone": "internal",
     "host_name": "machine1",
     "service": "compute"
   },
   {
     "zone": "internal",
     "host_name": "machine2",
     "service": "datastore"
   }]
};
```

currRuleJson = currRuleInput.rules;
currDataJson = currDataInput

1. Java API will start from `currRuleJson.main` which returns `<Machines>{Machines}</Machines>`

2. Java API will look for variable defined in {}. It finds `{Machines}` with no ‘$’. So API knows to replace `{Machines}` by the value of current rule Json key. i.e -> currRuleJson.Machines. The value of currRuleJson.Machines is `{hosts}`.

3. Java API finds `{hosts}` with ‘$’ then it will be replace by the value of current rule Json key. i.e -> currRuleJson.hosts. The value of currRuleJson.hosts is `{"main":"<Machine><name>{$host_name}</name><type>host</type></Machine>"}`

4. Java API finds currRuleJson.hosts is JsonObject so current rule will change as currRuleJson = currRuleJson.hosts

 API also finds currDataJson.hosts is JsonArray so API will process every iteration of hosts and apply currRuleJson.main (`<Machine><name>{$host_name}</name><type>host</type></Machine>`) to every host iteration.

currDataJson = currDataJson.hosts[i]

5. Java API finds `{$host_name}` so it will replace `{$host_name}` with the value of the property ‘host_name’ of current host object (iterated hosts array) i.e. `currDataJson.host_name`

so the output will be

```
<Machines>
   <Machine><name>machine1</name><type>host</type></Machine>
   <Machine><name>machine2</name><type>host</type></Machine>
</Machines>
```

# More Examples:

More examples can be found in test/resources/rules as unit test cases.

