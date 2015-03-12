
[![Build Status](https://travis-ci.org/BP2014W1/processeditor.svg)](https://travis-ci.org/BP2014W1/processeditor/)

Homepage
========
The homepage of the Process Editor Project is hosted here: http://frapu.de/code/processeditor/index.html
This fork extends the Process Editor Project with the capability to model and save PCM(Productive Case Management) models.

PCM Modelling
=============

If you want to learn more about modelling PCM Processes with the Process Editor see the [PCM document](docs/PCM.md).

ProcessEditor Core Packages
===========================
* com.inubit.research: Complex Process Workbench (Swing and WebModeler)
* com.frapu.net: Simple Java Canvas for creating ProcessModels incl. BPMN and Petri nets

Java Libraries:
==============
You can install the libraries manually or automatically.
You need an up to date installation of ant. To get all dependencies run the following command:

    ant init-ivy deps

To install the dependencies please do the following:
download separately and copy to lib folder
* log4j (https://logging.apache.org/log4j/1.2/download.html)
* javax.mail (http://www.oracle.com/technetwork/java/javamail/index.html)
* org.apache.commons.collections (http://commons.apache.org/proper/commons-collections/download_collections.cgi)
* camunda xml model (https://github.com/camunda/camunda-xml-model)
* camunda bpmn model (https://github.com/camunda/camunda-bpmn-model)

WebModeler requires ExtJS: Please download separately and copy ExtJs to www/js/ext (unzipped)
* ExtJS 4 (for Web Modeler, http://www.sencha.com/products/extjs/download/ext-js-4.2.1/2281)

Manual Building (Idea, Netbeans, etc.)
======================================
* Create a new Java 1.7 source project with the required libs as dependencies
* Add the "src" folder as source
* Add the "resources", "pics", "www" folder as resources (Idea) or source (Netbeans)
* Select "com.inubit.research.gui.Workbench" as main class for Workbench
* Select "com.inubit.research.server.ProcessEditorServer" as main class for Server

Ant Building
============
Use the build.xml with the following targets
* "clean-build-workbench": Builds a jar with a manifest file for the Workbench
* "clean-build-server": Builds a jar with a manifest file for the Server (incl. the "www" resources)
