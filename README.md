# Table of contents

1. [Introduction](#grimm)
	1. [Ecosystem of grimm](#ecosystem-of-grimm)
	2. [Releases](#releases)
2. [Overview](#overview)
3. [Use grimm](#use-grimm)
	1. [Quick start](#start-grimm-in-10-steps)	
	2. [Parameters files](#parameters-file)
		1. [Create](#create-a-pre-filled-parameters-file)
		2. [Example](#example-of-parameters-file) 
	3. [Configuration file](#configuration-file)
		1. [Create](#create-a-pre-filled-configuration-file)
		2. [Example](#example-of-configuration-file)
	4. [More help and tutorials](#more-help-and-tutorials)

# grimm

grimm is an automated model generation tool. It automatically creates instance for EMF meta-models. 

This document describes all what you need to know about **grimm**.

## Ecosystem of grimm

grimm is the centerpiece of more other works (papers and tools) done by the same team. This section gives a small overview of all this work.

- [last news](https://www.adel-ferdjoukh.ovh/) to get the last news about grimm and its ecosystem.
- [grimm](https://github.com/ferdjoukh/grimm)
- [COMODI](https://github.com/ferdjoukh/comodi) (**CO**unting **MO**del **DI**fferences) is a tool for comparing models.
- [TIWIZI](https://github.com/ferdjoukh/tiwizi/) is a fault localizer for debugging meta-models.

## Releases

- [v6.1-d29112018](https://github.com/ferdjoukh/grimm/releases/tag/v6.1-d29112018) (November 29th 2018). This version corrects the following bugs:
	- Clear workspace variables when creating more than 1 solution.
	- Start building by containment tree than normal references.
	- When creating a link *A->B*, check that *A* and *B* are contained by an other resource, otherwise, *xmi* file cannot be saved.

- [v6.0-d23112018](https://github.com/ferdjoukh/grimm/releases/tag/v6.0-d23112018) (November 23th 2018). This version add following features:

	- Generation of Chromosomes for Genetic Algorithms. it consists of a .chr file generated when a dot file is asked. This is an example of [.chr](https://github.com/ferdjoukh/grimm/blob/master/CHR/Graph-211339-2211181.chr) files.
	- validate (v): add a new option for grimm. It is used to check the validity of a given chromosome (.chr file). If the chromosome is valid then a model is generated (dot format). To use this option, write the following command: 

		`java -jar grimm.jar v your-chromosome.chr`

- [v5.0-d20112018](https://github.com/ferdjoukh/grimm/releases/tag/v5.0-d20112018) (November 20th 2018). This version add following features:

	- Adding random diversity when assigning values for attributes of type: EEnum, EInt, EBoolean and EString.
	- EString attributes have 2 default configuration modes:
		- **name** is applied for name attributes. Each attribute is named following his class and EObject ID (eg. Method4).
		- **random** is applied for other attributes. A random string is generated (3 to 10 alphabetic symbols).
	- Allow the users to give custom domains for EInt and EString by using a [configuration file](#configuration-file) (.grimm)	
		- For **EInt** attributes, users can give either an interval (25..30) or a list of values (-1 0 1). This is useful for example in UML meta-model to set up the lower and upper bound of an Association.
		- For **EString** attributes, users can give a list of values. It is interesting to get more customisable models.
	- Variables that represent attributes in CSP are removed. Instantiation is done as a post-processing (after the CSP solver).
	- Default interval for EInt attributes us set to 1..100 (can be changes by using a config file).
	- Corrected bug: Attribute types are printed in config files as a reminder.
	- Corrected bug: A generated pre-filled config file is now consistent and can be used to generate an empty model.
	- Corrected bug: Boolean and EEnum attribute are removed from config files.	
	- Corrected bug: created config files are stored in the folder chosen by the user and not in *rootClass* folder.
	- Corrected bug: problem of instantiation for 0..* references when reference UB is set to 1.	


- [v4.0-d15112018](https://github.com/ferdjoukh/grimm/releases/tag/v4.0-d15112018) (November 15th 2018). This version add following features:

	- Add specific processing for containment references. Now it is different from classic references and not included in the produced CSP instances.
	- Add support for deep containment references (>1). Now you can have containment references between two classes (not only root class).
	- Add support for boolean, integer, string and enum attributes. Moreover, randomness is added while instantiating attributes.
	- Diversity is added in treating EOpposite references. Now the created GCC has diverse upper bound. This makes the generated models different even when the same configuration is used.
	- Adding the possibility of generating 0 instances for a given class (you can do that by using a config file).
	- Corrected bugs in ConfigrationFileReader class. Now the order of classes in a config file is not important.
	- Corrected bug: unchangeable references and attributes are not considered any more.

- [v3.0-d9112018](https://github.com/ferdjoukh/grimm/releases/tag/v3.0-d9112018) (November 9th 2018). This versione add or corrects the following features:
	- Adding an Exception when a given rootClass is incorrect.
	- Correcting some issues (related to tricky meta-models as ecore.ecore): linking EObjects in a hierarchy of inheritance, checking the superType of an EObject instead of just comparing class names before linking. 
	- Adding more diversity while connecting EObjects. Currently, diverse EObjects are chosen randomly.
	- Corrected problem of attributes typing (== replaced by equals). 

- [v2.0-d2792018](https://github.com/ferdjoukh/grimm/releases/tag/v2.0-d2792018) (September 27th 2018). This version adds the following features to grimm:

	- Changing the way of giving input parameters: now grimm creates a pre-filled *.params* file in which you give: meta-model, root class, OCL file, generation parameters (quick mode or configuration file), number of desired solutions, output format type (xmi or dot).
	- 4 simple command line options: help **(h or help)**, parameters File creation **(p or parameter)**, configuration file creation **(c or config)** and generation of models **(g or generation)**.
	- Generation of several solutions in one solver call (number of solutions is specified in *.params* file)
	- Basic Fault Localization based on a system of Exceptions (not found meta-model, config file, OCl file , CSP solver, etc)
	- Reorganization of the source code: javadoc, creation of new packages. 

- [v1.0-d792018](https://github.com/ferdjoukh/grimm/releases/tag/v1.0-d792018) (September 7th 2018). This version contains all the code that was written between 2013 and 2017 on grimm. This means it is a classic version of grimm (Ecore meta-model, partial OCL support and 1-solution generation).    

# Overview

# Use grimm

## start grimm in 10 steps

1. Go to the release page [here](https://github.com/ferdjoukh/grimm/releases)
2. Choose the desired release (*the last release is recommended*) and download it (zip file).Unpack the zip file.

3. Inside the folder, another zip called **grimm-executable.zip** contains: **a runnable jar**, **the CSP solver** (*abssol.jar*), and **examples**: meta-models (.ecore, .ocl files), parameters-files (.params) and config-files (.grimm).

4. Unpack this zip.
5. Now you are ready to start using **grimm**.
6. Show help by running this command in a terminal:

	`java -jar grimm.jar`

8. Run this quick start command to verify that everything is okay:

	`java -jar grimm.jar g parameters-files/quick-tests/test1-quick-xmi.params`

	1. If everything worked well, two first models are generated and stored in the following folder:

		`currentfolder/Compo/`	

9. Install **graphviz** if you want to create object diagrams for your generated models.

	`sudo apt-get install graphivz` (*on ubuntu for example*)

10. Run this 2nd quick start command to verify that **graphviz** is running without problems:

	`java -jar grimm.jar g parameters-files/quick-tests/test2-quick-dot.params`

	1. Again a model is generated and stored in:

		`currentfolder/Compo/`

Now you are ready to use grimm. If you have your own meta-model, you can create you own [Parameters file](#parameters-file) and a [Configuration File](#configuration-file) in order to start generation.

## Parameters File

It is a type of file that grimm needs to generate models. It contains the main information on the meta-model and the wished output format.

These are the list of information that are needed:

1. meta-model (mandatory)
2. root Class (mandatory)
3. OCL file (optional)
4. Size parameters mode:
	1. Quick mode: give lower bound and upper bound for classes and an upper bound for unbounded references. This is the default mode. 
	2. Config mode: in this case, you need to specify a [configuration file](#configuration-file) that contains more detailed information.
5. Number of wished solutions (default 1)
6. Output format: *xmi* models or *dot* graphical object diagrams.
7. CSP solver (*currently only abscon solver is possible*). 	

### Create a pre-filled Parameters file

You can create a pre-filled Parameters file:

	java -jar grimm.jar p your-file.params

**Remark** It is preferable to name your Parameters file: *file.params* but this is not mandatory.

### Example of Parameters file

```matlab
# This file contains all the generation parameters of GRIMM tool
#
# Fill the file with your own information
#   + are mondatory
#   - must be filled or removed
#   (1) and (2) block must not appear at the same time
#
+meta-model =examples/test.ecore
+rootClass =Compo
#(1)
lowerBound for classes =2
upperBound for classes =4
upperBound for references =2
#
#
number of solutions =1
#
output format =dot
CSP solver =abscon
```	

## Configuration File

Configuration files contain detailed information about the size of desired models.

These information are:

1. number of instances for each class
2. custom domains for attributes (EInt, EString, EBoolean and EEnum are supported)
3. bound for unbounded references

### Create a pre-filled Configuration file

You can create a pre-filled Configuration file:

	java -jar grimm.jar c config-file.grimm metamodel.ecore rootClass

### Example of Configuration file

```matlab
% Configuration file for grimm tool 
%	Please specify detailed information on your models:
%		(1) precise number of class intances
%		(2) domain for attributes
%		(3) reference upper bound
%---------------------------------
% Number of instances for Classes
%---------------------------------
Street=2
Boulevard=2
Pedestrian=5
Garden=1
Square=2
%---------------------------------
% Domains of the features
%---------------------------------
%	Strings: choose: random, name or give a list of values (space separated)
%	Integer: choose: 1..100, custom interval or  list of values (space separated)
%---------------------------------
% String
%--------
map/name=Alger Oran Tizi Bejaia Adrar
map/country=Algeria
Street/name=Didouche Abane-Ramdane Amirouche
Street/district=random
Boulevard/name=name
Boulevard/district=random
Pedestrian/name=name
Pedestrian/district=random
Garden/name=name
Square/name=name
%---------------------------------
% Integer
%---------
Street/length=100 900 800 700 2200
Boulevard/length=1000 2000 3000 4000 3500
Pedestrian/length=100 20 50 65
%---------------------------------
% References upper bound
%---------------------------------
RefsBound=3
```

## More help and tutorials

Here you can find additional tutorials to help you while using grimm.

1. [How to open xmi files with Sample Reflective Ecore Model Editor](https://github.com/ferdjoukh/grimm/blob/master/documentation/how-to-open-xmi-files-in-Eclipse.md)
