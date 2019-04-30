# Formal Methods Workbench 

The [Formal Methods Workbench](https://github.com/loonwerks/formal-methods-workbench.git) is a collection of standalone tools and [OSATE2](http://osate.org/) plugins for reasoning about safety and security properties of information models.

## Building and Installing the OSATE2 Plugins

### Prerequisites

The build for the tools require the Java JDK 8 and the Maven build tool. A recent version of [OSATE2](http://osate.org/) is also needed.

**Java JDK 8**: Download and install [Java 8 JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html) if it is not already on the build machine. It does *not* need to be the default JDK, but it does need to exist on the build machine. Mac OSX users might consider [Homebrew](https://brew.sh/): `brew cask install java8`. The new Java 8 JDK is installed at `/Library/Java/JavaVirtualMachines/jdk1.8.0_192.jdk/`.

**Maven**: download and install a [maven](https://maven.apache.org/index.html). The build for the extensions is managed by the **mvn** Maven command-line tool, so it needs to be on the terminal's search path. Mac OSX users might consider [Homebrew](https://brew.sh/): `brew install maven`.

After installing **mvn**, make sure it is using [Java 8 JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html):

``` 
mvn -version
Apache Maven 3.6.0 (97c98ec64a1fdfee7767ce5ffb20918da4f719f3; 2018-10-24T12:41:47-06:00)
Maven home: /usr/local/Cellar/maven/3.6.0/libexec
Java version: 11.0.1, vendor: Oracle Corporation, runtime: /Library/Java/JavaVirtualMachines/jdk-11.0.1.jdk/Contents/Home
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "10.13.6", arch: "x86_64", family: "mac"
```

If the wrong JDK is specified, then set the `JAVA_HOME` environment variable to point to the installed [Java 8 JDK](https://www.oracle.com/technetwork/java/javase/downloads/jdk8-downloads-2133151.html), or set the variable for the duration of the **mvn** command with

```
JAVA_HOME=/Library/Java/JavaVirtualMachines/jdk1.8.0_192.jdk/Contents/Home/ mvn -version
Apache Maven 3.6.0 (97c98ec64a1fdfee7767ce5ffb20918da4f719f3; 2018-10-24T12:41:47-06:00)
Maven home: /usr/local/Cellar/maven/3.6.0/libexec
Java version: 1.8.0_192, vendor: Oracle Corporation, runtime: /Library/Java/JavaVirtualMachines/jdk1.8.0_192.jdk/Contents/Home/jre
Default locale: en_US, platform encoding: UTF-8
OS name: "mac os x", version: "10.13.6", arch: "x86_64", family: "mac"
```

**OSATE2**: download and install the latest stable version of [OSATE2](http://osate.org/). Mac OSX users may run into issues with OSX's quarantine system:

```osate2 can't be opened. You should move it to trash.
``` 

The fix is to whitelist the application:

```
$ sudo xattr -rd com.apple.quarantine osate2.app/
```

Also, if Java 8 is not the default JDK on the system, then *osate2* gives the error `Failed to create the Java Virtual Machine`. The fix is to edit *osate2.app/Contents/Infor.plist* and set the *-vm* clause in the **Eclipse** key to point to the **java** executable in the installed Java 8 JDK folder.

```
	<key>Eclipse</key>
		<array>
        	<string>-vm</string><string>/Library/Java/JavaVirtualMachines/jdk1.8.0_192.jdk/Contents/Home/bin/java</string>
			<string>-keyring</string>
      <string>~/.eclipse_keyring</string>
```

These should enable **osate2** to launch.

### Getting the source

Clone the [Formal Methods Workbench](https://github.com/loonwerks/formal-methods-workbench.git) repository on your machine.

```
git clone https://github.com/loonwerks/formal-methods-workbench.git
```

### Building the plugins

Once **mvn** is using the right JDK, change to the *formal-methods-workbench* directory and build the plugins and IDE:
```
$ cd formal-methods-workbench
$ mvn clean install
```

Although there are several warnings in the build, there should not be any errors.

### Adding the Plugins to OSATE2

Install the plugins to [OSATE2](http://osate.org/).
The build creates the plugin bundles for key tools in the `formal-methods-workbench/tools` subdirectory.  Each of the following plugins can be added to [OSATE2](http://osate.org/):

   * Resolute: architectural assurance builder tool for AADL
   * Z3: SMT solver
   * AADL Simulator
   * CASE: ???
   * AGREE: assume-guarantee-style model checker for AADL models

After starting [OSATE2](http://osate.org/), the general process for each tool is

   1. Select *Help --> Install New Software...* and click **Add** in the dialogue.
   2. State the name and then click **Archive...**
   3. In the finder window, navigate to the appropriate directory to install the matching plugin:

      * **Resolute**: /formal-methods-workbench/tools/resolute/repository/target/com.collins.fmw.resolute.repository-1.0.0-SNAPSHOT.zip
      * **z3**: /formal-methods-workbench/tools/z3/repository/target/com.collins.fmw.z3.repository-1.0.0-SNAPSHOT.zip
      * **AADL Simulator**: /formal-methods-workbench/tools/simulator/repository/target/com.collins.fmw.aadlsimulator.repository-1.0.0-SNAPSHOT.zip
      * **CASE**: /formal-methods-workbench/tools/case/repository/target/com.collins.fmw.cyres.repository-0.0.0.zip
      * **AGREE**: /formal-methods-workbench/tools/agree/repository/target/com.rockwellcollins.atc.agree.repository-2.4.0-SNAPSHOT.zip 

   4. Click **Add**, select the products to install, and then follow the dialogues.

## Testing the plugins

What needs to go here?

## Source Code Organization
The code is organized as a hierarchy of modules that may be built individually.
Each module has its own configunartion file pom.xml. 
If a module depends on building a submodule, it specifies the location of the submodules in its pom.xml file.
It also typically contains the submodule source code in a subdirectory as well.
The exception is for submodules that are from external repos, such as smaccm and ostate2.
```
formal-methods-workbench
|- com.collins.fmw.cyres
|  |- json.plugin
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

Install
=====
1. Download the latest version of OSATE: (http://www.aadl.info/aadl/osate/stable/).
2. Start OSATE and go to "Help -> Install New Software..."
3. Click the "Add..." button in the upper right hand corner and add this URL as an update site: (https://raw.githubusercontent.com/smaccm/update-site/master/site.xml)
4. Click the box labeled "SMACCM", click "Finish", and proceed through the dialog.

Notes
=====
The AGREE plugin is only packaged with the SMTInterpol SMT solver. If you want to use other solvers ([Z3](https://github.com/Z3Prover/z3),
[Yices (version 1)](http://yices.csl.sri.com/download-yices1.shtml), 
[Yices 2](http://yices.csl.sri.com/index.shtml),
[CVC4](http://cvc4.cs.nyu.edu/web/), or
[MathSAT](http://mathsat.fbk.eu/)) you will need to obtain them separately and set your PATH environment variable to point at their location.  In order to perform Realizability Analysis you must have Z3 installed.

**If you have trouble installing the updates you might need to run OSATE as administrator.**

Generated Code
=====
Commit generated source code separately from manually created code, to make reviewing changes easier.  
