# Formal Methods Workbench 

The Formal Methods Workbench is a collection of eclipse based tools for reasoning about safety and security properties of information models.
This includes a complete IDE as well as plugins. 

## Getting Started
Clone this repository on your machine.
Clone the smaccm and osate2 repos to the same location as your local copy of this repo.

```
git clone https://github.com/loonwerks/formal-methods-workbench.git
git clone https://github.com/smaccm/smaccm.git
git clone https://github.com/osate/osate2.git
```

### Prerequisites

Download a copy of the build tool maven: https://maven.apache.org/index.html

### Installing

Build the plugins and IDE with maven.


change to the formal methods workbench directory.
```
cd formal-methods-workbench
```

Install using maven
```
mvn clean install
```
This will create the runnable IDE and package various plugin jars, each within a target directory, within its respective source code directory.

The runnable IDE will be located at __./com.collins.fmw.ide/target/products__.

Note that the build depends on osate2 and smaccm, and their build configurations require that their repos have all changes commited.

## Source Code Organization
The code is organized as a hierarchy of modules that may be built individually.
Each module has its own configunartion file pom.xml. 
If a module depends on building a submodule, it specifies the location of the submodules in its pom.xml file.
It also typically contains the submodule source code in a subdirectory as well.
The exception is for submodules that are from external repos, such as smaccm and ostate2.
```
formal-methods-workbench
|- com.collins.fmw.cyres
|  |- agree.json.plugin
|  |  `- pom.xml
|  |
|  |- architecture.plugin
|  |  `- pom.xml
|  |
|  |- feature
|  |  `- pom.xml
|  |
|  |- repository
|  |  `- pom.xml
|  |
|  |- splat.plugin
|  |  `- pom.xml
|  |
|  |- toolcheck.plugin
|  |  `- pom.xml
|  |
|  `- util.plugin
|     `- pom.xml
|   
|- com.collins.fmw.ide
|  `- pom.xml
|   
|- com.collins.fmw.json
|  `- pom.xml
|   
`- pom.xml
```

## Running the tests

TODO: Explain how to run the automated tests for this system

### Break down into end to end tests

TODO: Explain what these tests test and why

```
TODO: Give an example
```

### And coding style tests

TODO: Explain what these tests test and why

```
TODO: Give an example
```

## Deployment

TODO: Add additional notes about how to deploy this on a live system

## Built With

TODO

## Contributing

TODO 
## Versioning

TODO

## Authors

TODO

## License

TODO

## Acknowledgments

TODO