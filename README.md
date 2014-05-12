PubMedIndexing
==============

Compilation:

Download the following jars from the web and add to your project repository
1) lucene-analyzers-common-4.5.0.jar
2) lucene-core-4.5.0.jar
3) lucene-queries-4.5.0.jar
4) lucene-queryparser-4.5.0.jar
5) lucene-sandbox-4.5.0.jar

Running:

In the package edu.iub.pubmed.properties, change the following paths before executing the main method in
package edu.iub.pubmed.main
loadDataSet: Enter the path which has the whole dataset
luceneIndexPath: Enter the path for the lucene index
