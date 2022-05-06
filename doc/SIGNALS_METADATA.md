Journal:
{
  links: IGNORED
  data: FLATTERED. Array of only one member
  [ 
    type: IMPLICIT
    id: IGNORED
    links: IGNORED
    attributes:
    {
      id: IGNORED
      eid: IGNORED
      name: USED
      description: USED
      createdAt: TO_USE (GMZ format)
      editedAt: TO_USE (GMZ format)
      type: IMPLICIT (already imported)
      digest: ? (a hash digest by ? method)
      fields: TO_USE
      {
        Confidential: 
        {
          value: (yes/no)
        }
        Department: 
        {
          value: ("Echavarren")
        },
        Description: 
        {
          value: ALREADY_USED
        }
        Name: 
        {
          value: ALREADY_USED 
        }
      }
    }
    relationships: TO_USE 
    {
      createdBy: TO_USE 
      {
        links: TO_USE ? 
        {
          self: "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/users/183"
        }
        "data": 
        {
          "type": "user",
          "id": "183"
        }
      }
      editedBy: TO_USE ?
      {
        "links": 
        {
          "self": "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/users/183"
        }
        "data": 
        {
          "type": "user",
          "id": "183"
        }
      },
      owner: TO_USE ?
      {
        "links": 
        {
          "self": "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/users/183"
        },
        "data": 
        {
          "type": "user",
          "id": "183"
        }
      },
      "children": IGNORE 
      {
        "links": 
        {
          "self": "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities/journal:ddac1092-5f55-4952-a5f1-7f2c553fef46/children"
        }
      }
    }
  ]
}
```json
{
  "links": {
    "self": "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities?includeTypes=journal&include=children, owner&page[offset]=7&page[limit]=1",
    "first": "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities?includeTypes=journal&include=children, owner&page[offset]=0&page[limit]=1",
    "prev": "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities?includeTypes=journal&include=children, owner&page[offset]=6&page[limit]=1",
    "next": "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities?includeTypes=journal&include=children, owner&page[offset]=8&page[limit]=1"
  },
  "data": [
    {
      "type": "entity",
      "id": "journal:ddac1092-5f55-4952-a5f1-7f2c553fef46",
      "links": {
        "self": "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities/journal:ddac1092-5f55-4952-a5f1-7f2c553fef46"
      },
      "attributes": {
        "id": "journal:ddac1092-5f55-4952-a5f1-7f2c553fef46",
        "eid": "journal:ddac1092-5f55-4952-a5f1-7f2c553fef46",
        "name": "Linear Acenes",
        "description": "",
        "createdAt": "2020-05-27T19:52:54.871Z",
        "editedAt": "2020-05-27T19:52:54.871Z",
        "type": "journal",
        "digest": "43254827",
        "fields": {
          "Confidential": {
            "value": "No"
          },
          "Department": {
            "value": "Echavarren"
          },
          "Description": {
            "value": ""
          },
          "Name": {
            "value": "Linear Acenes"
          }
        }
      },
      "relationships": {
        "createdBy": {
          "links": {
            "self": "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/users/183"
          },
          "data": {
            "type": "user",
            "id": "183"
          }
        },
        "editedBy": {
          "links": {
            "self": "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/users/183"
          },
          "data": {
            "type": "user",
            "id": "183"
          }
        },
        "owner": {
          "links": {
            "self": "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/users/183"
          },
          "data": {
            "type": "user",
            "id": "183"
          }
        },
        "children": {
          "links": {
            "self": "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/entities/journal:ddac1092-5f55-4952-a5f1-7f2c553fef46/children"
          }
        }
      }
    }
  ],
  "included": [
    {
      "type": "user",
      "id": "183",
      "links": {
        "self": "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/users/183"
      },
      "attributes": {
        "userId": "183",
        "userName": "ostoica@iciq.es",
        "alias": "ostoica",
        "email": "ostoica@iciq.es",
        "firstName": "Otilia",
        "lastName": "Stoica"
      },
      "relationships": {
        "systemGroups": {
          "links": {
            "self": "https://iciq.signalsnotebook.perkinelmercloud.eu/api/rest/v1.0/users/183/systemGroups"
          }
        }
      }
    }
  ]
}
```
