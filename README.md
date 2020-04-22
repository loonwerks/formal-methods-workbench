# Formal Methods Workbench 

The [Formal Methods Workbench](https://github.com/loonwerks/formal-methods-workbench.git) is the aggregation of standalone tools and [OSATE2](http://osate.org/) plugins for reasoning about safety and security properties of information models.

The following plugins compose the Formal Methods Workbench:

* [OSATE](https://osate.org)
* [BriefCASE](https://github.com/loonwerks/BriefCase.git)
* [HAMR](https://github.com/sireum/hamr-codegen.git)
* [AGREE](https://github.com/loonwerks/AGREE.git)
* [Resolute](https://github.com/loonwerks/Resolute.git)
* [JKind](https://github.com/loonwerks/jkind-plugin.git)
* [Z3](https://github.com/loonwerks/z3-plugin.git)

## Building and Installing the OSATE2 Plugins

### Prerequisites

Note: As Oracle now requires a paid subscription license to use their JDK, work is presenly underway investigating use of OpenJDK instead.  Initial results are positive, but presently the only supported JDK is the Oracle JDK 8.

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

### Building the plugins and IDE product

Presently, the formal methods workbench is contained in three repositories.  This repository contains the Briefcase plugins and the top-level product build.  The AGREE tool, AADL simulator and test case generator are now contained in the [AGREE](https://github.com/loonwerks/AGREE.git) repository and the Resolute assurance case tool in the [Resolute](https://github.com/loonwerks/Resolute.git) repository.  Ordinarily, one would include AGREE and Resolute as dependencies accessed via P2 repositories included through the Tycho/Maven build.  However, we do not (yet) have these AGREE and Resolute available through a publicly-accessible P2 repository.  Thus, these must be still be included via building them on the local filesystem. Steps are given below.

Once **mvn** is using the right JDK, change to the *formal-methods-workbench/tools* directory and build the plugins and IDE:
```
$ cd formal-methods-workbench/tools
$ mvn clean verify
```

Although there are several warnings in the build, there should not be any errors.

The build process packages the formal methods workbench plugins as individual P2 repositories and as a complete integrated development environment product.  The IDE product includes OSATE with all of the individual plugins pre-installed.  Compressed archives for Linux, Windows and MacOSX may be found in the *formal-methods-workbench/tools/ide/target/products* directory.

## Continuous Integration / Continuous Deployment

Continuous integration is conducted through [Travis CI](https://travis-ci.org/loonwerks/formal-methods-workbench) as an open source project.  Presently builds are completed once per day, aggregating the latest development versions of the individual plugins comprising the Formal Methods Workbench and their dependencies.  As the development stabilizes, it is intended that changes checked into the repository will trigger automated builds.

As Travis CI is used for CI/CD, the configuration file for the builds is [.travis.yml](https://github.com/loonwerks/formal-methods-workbench/blob/master/.travis.yml).  This configuration file specifies invocation of maven in the *formal-methods-workbench/tools* directory to conduct all steps of gathering dependencies, compilation, test and packaging.  Presntly two deployments are specified.  The first of these serves the daily development build, storing the resuling P2 plugin repository and the IDE product in the [Releases](https://github.com/loonwerks/formal-methods-workbench/releases) page of this repository.  The second manages the daily builds, deleting stale daily releases.  Necessart support scripts, implemented in Python3, are kept in the *formal-methods-workbench/.travis* directory.

## Built With

Builds are managed through the [Eclipse Tycho](https://www.eclipse.org/tycho/) buid system.  The top-level build is presently launched from the *formal-methods-workbench/tools* directory where an aggregator project collects the various subprojects comprising the Formal Methods Workbench IDE.  The majority of these plugins are presently contained within this repository and directly composed as modules under the top-level build.  However, the Assume-Guarantee REasoning Environment (AGREE) and the Resolute Assurance Case Analysis are (soon to be) maintained in separate repositories and included as dependencies through Tycho/Maven.

## Contributing

Presently direct contributions to this repository are limited to members of the DARPA CASE program team.  However, we welcome suggestions for improvement submitted via the [Issues](https://github.com/loonwerks/formal-methods-workbench/issues) page.  After disucssion with the team we may welcome pull requests improving or implementing features.  Thanks for your interest in improving the state of the practice in cyber-aware system engineering.

## Versioning

As the Formal Methods Workbench is still in the early stages of development and not all planned plugins have yet been integrated, the current version is 0.1.0-SNAPSHOT.  It is likely that an official release will be made for the next tool evaluation under the DARPA CASE program.

The various plugins immediately composed in this repository presently track their version numbers with that of the aggregate IDE.  Dependency plugins such as AGREE and Resolute maintain their own version number scheme.

Presently maintenance of version numbers is completed manually.  It is the intent that the [Tycho Release Workflow](https://wiki.eclipse.org/Tycho/Release_Workflow) will be integrated to automate the release and version numbering process.

## License

This tool is owned and licensed by Collins Aerospace.  A BSD-like 3-clause license is described in the [LICENSE](https://github.com/loonwerks/formal-methods-workbench/blob/master/LICENSE) file.

## Acknowledgments

TODO

Install
=====
1. Download the appropriate version of Formal Methods Workbench: (https://github.com/loonwerks/formal-methods-workbench/releases).
2. Uncompress the archive to a directory of your choice.
3. Run the FmIDE application.  Mac users may need remove a quarantine marker on the executable.

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
