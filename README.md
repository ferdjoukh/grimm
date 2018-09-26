# Table of contents

1. [Introduction](#grimm)
	1. [Ecosystem of grimm](#ecosystem-of-grimm)
	2. [Releases](#releases)
2. [Overview](#overview)
3. [Use grimm](#use-grimm)
	1. [Quick start](#quick-start)	
	2. [Documentation](#documentation)
	3. [Help and tutorials](#help-and-tutorials)

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

# Overview

# Use grimm

## Quick start

1. Go to the release page [here](https://github.com/ferdjoukh/grimm/releases)
2. Choose the desired release and download it (zip file).
3. Unpack the zip file.
4. Inside the obtained folder, another zip called **grimm-executable.zip** contains: the runnable jar and the CSP solver (*abssol.jar*), and some example meta-models.
5. Unpack this zip.
6. Now you are ready to start using **grimm**.
7. Show help by running this command in a terminal:

	`java -jar grimm.jar`

8. The quick start commands are shown in the help page of grimm.
9. Run this quick start command to verify that everything is okay:

	`java -jar grimm.jar -mm=test.ecore -root=Compo -lb=2 -ub=2 -rb=4 -xmi`

	1. If everything worked well, a first model is generated and stored in the following folder:

		`currentfolder/Compo/`	

10. Install **graphviz** if you want to create object diagrams for your generated models.

	`sudo apt-get install graphivz` (on ubuntu for example)

11. Run this 2nd quick start command to verify that **graphviz** is running without problems:

	`java -jar grimm.jar -mm=test.ecore -root=Compo -lb=2 -ub=2 -rb=4 -dot`

	1. Again a model is generated and stored in:

		`currentfolder/Compo/`

## Documentation



## Help and tutorials

Here you can find some points to help you while using grimm.

1. [How to open xmi files with Sample Reflective Ecore Model Editor](https://github.com/ferdjoukh/grimm/blob/master/documentation/how-to-open-xmi-files-in-Eclipse.md)
