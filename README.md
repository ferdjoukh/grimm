# Table of contents

1. [Introduction](#grimm)
	1. [Ecosystem of grimm](#ecosystem-of-grimm)
	2. [Releases](#releases)
2. [Overview](#overview)
3. [Use grimm](#use-grimm)
	1. [Quick start](#quick-start)	
	2. [Parameters files](#parameters-file)
	3. [Configuration file](#configuration-file)
	4. [More help and tutorials](#more-help-and-tutorials)

# grimm

grimm is an automated model generation tool. It automatically creates instance for EMF meta-models. 

This document describes all what you need to know about **grimm**.

## Ecosystem of grimm

grimm is the centerpiece of more other works (papers and tools) done by the same team. This section gives a small overview of all this work.

- [Website](https://adel-ferdjoukh.ovh/) to get the last news about grimm and its ecosystem.
- [COMODI](https://adel-ferdjoukh.ovh/research/comodi/) (**CO**unting **MO**del **DI**fferences) is a tool for comparing models.
- [TIWIZI](https://adel-ferdjoukh.ovh/research/tiwizi/) is a fault localizer for debugging meta-models.

## Releases


- [v1.0-d792018](https://github.com/ferdjoukh/grimm/releases/tag/v1.0-d792018) (September 7th 2018). This version contains all the code that was written between 2013 and 2017 on grimm. This means it is a classic version of grimm (Ecore meta-model, partial OCL support and 1-solution generation).    

- This version adds the following features to grimm:

	- Changing the way of giving input parameters: now grimm creates a pre-filled *.params* file in which you give: meta-model, root class, OCL file, generation parameters (quick mode or configuration file), number of desired solutions, output format type (xmi or dot).
	- 4 simple command line options: help **(h or help)**, parameters File creation **(p or parameter)**, configuration file creation **(c or config)** and generation of models **(g or generation)**.
	- Generation of several solutions in one solver call (number of solutions is specified in *.params* file)
	- Basic Fault Localization based on a system of Exceptions (not found meta-model, config file, OCl file ,...)
	- Reorganization of the source code: javadoc, creation of new packages. 

# Overview

# Use grimm

## start grimm in 10 steps

1. Go to the release page [here](https://github.com/ferdjoukh/grimm/releases)
2. Choose the desired release (*the last release is recommended*) and download it (zip file).Unpack the zip file.

3. Inside the folder, another zip called **grimm-executable.zip** contains: **a runnable jar**, **the CSP solver** (*abssol.jar*), and **examples**: Meta-models (.ecore, .ocl files), Parameters files (.params) and Configuration files (.grimm).

4. Unpack this zip.
5. Now you are ready to start using **grimm**.
6. Show help by running this command in a terminal:

	`java -jar grimm.jar`

8. Run this quick start command to verify that everything is okay:

	`java -jar grimm.jar g examples/testMM1.params`

	1. If everything worked well, two first models are generated and stored in the following folder:

		`currentfolder/Compo/`	

9. Install **graphviz** if you want to create object diagrams for your generated models.

	`sudo apt-get install graphivz` (on ubuntu for example)

10. Run this 2nd quick start command to verify that **graphviz** is running without problems:

	`java -jar grimm.jar g examples/testMM2.params`

	1. Again a model is generated and stored in:

		`currentfolder/Compo/`

Now you are ready to use grimm. If you have your own meta-model, you can creates you own [Parameters file](#parameters-file) and a [Configuration File](#configuration-file) in order to start generation.

## Parameters File

## Configuration File

## More help and tutorials

Here you can find additional tutorials to help you while using grimm.

1. [How to open xmi files with Sample Reflective Ecore Model Editor](https://github.com/ferdjoukh/grimm/blob/master/documentation/how-to-open-xmi-files-in-Eclipse.md)
