db.users.insert([
  {
    "username": "Inactive Doe",
    "email": "inactive.doe@example.com",
    "password": "$2a$10$lj455voWCw14S0MVJ0eGn.tBVzn0PUPGqBhXE.2/FBS4t42vOu4c2",
    "organization": "Example Inc.",
    "roles": [
      "ROLE_USER",
      "ROLE_OWNER"
    ],
    "active" : false
  },
  {
    "username": "John Doe",
    "email": "john.doe@example.com",
    "password": "$2a$10$lj455voWCw14S0MVJ0eGn.tBVzn0PUPGqBhXE.2/FBS4t42vOu4c2",
    "organization": "Example Inc.",
    "roles": [
      "ROLE_USER",
      "ROLE_ADMIN"
    ],
    "active" : true
  },
  {
    "username": "Mary Doe",
    "email": "mary.doe@example.com",
    "password": "$2a$10$lj455voWCw14S0MVJ0eGn.tBVzn0PUPGqBhXE.2/FBS4t42vOu4c2",
    "organization": "Example Inc.",
    "roles": [
      "ROLE_USER"
    ],
    "active" : true
  },
  {
    "username": "Pete Doe",
    "email": "pete.doe@example.com",
    "password": "$2a$10$lj455voWCw14S0MVJ0eGn.tBVzn0PUPGqBhXE.2/FBS4t42vOu4c2",
    "organization": "Example Inc.",
    "roles": [
      "ROLE_USER",
      "ROLE_OWNER"
    ],
    "active" : true
  }
])