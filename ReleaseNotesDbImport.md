# dbimport #

## 2.5.17 (May 2014) ##
  * ExcelImporterSpec.getSheetName() - public method added

## 2.5.14.1 (May 2013) ##
  * NEW: SharedExcelRowReaderFactory: helps to implement multiple imports on the same excel workbook.
  * NEW: ExcelGenerator: convenience class to write data as excel worksheet.
  * NEW: ExcelImporterSpec: convenience ImporterSpec for importing Excel spreadsheets.
  * NEW: ExcelImportProcessor
  * ExcelRowReader: supports sheetName and keepOpen
  * NEW: ExcelUtils: provides commonly used methods to read data from excel cells

## 2.5.13, 2.5.14 ##
do not use these releases

## 2.5.11 ##
  * ExcelRowReader: added code to remove the current row from excel sheet during import
  * ExcelRowReader: added code to remove a column from a excel sheet
  * added getter Importer.getProcessor()
  * using slf4j instead of commons-logging or log4j-api directly

## 2.5.10 ##
> several dependencies upgraded and use of deprecated APIs reduced:

> junit 4.8.2-> 4.10 dbunit 2.2 -> 2.4.8 jdom 1.0 -> 1.1 groovy-all 1.6.2 -> 1.8.6 commons-lang 2.4 -> 2.6 , commons-io

## 2.5.0 (July 2011) ##
  * Changed artifactId from com.agimatec to de.viaboxx
  * Published artifacts to maven central.

## 2.4.0 ##
no changes, maven version adjusted

## 2.3.3 (upcoming, current trunk) ##
ADDED:
  * new class 'ImportJob' simplifies API of dbimport
CHANGED:
  * log4j upgrade from 1.2.14 to 1.2.16
  * upgrade to poi3.7 (only maven dependency changed, no API changes)
FIXED:
  * Excel-support: bug fixes reading value from formular cells

## 2.3.1 (Febr. 2010) ##
The 2.3.x versions of agimatec-tools support org.apache.poi version 3.6.
If you need dbimport with this pos version, checkout the trunk from svn or load one of the 2.3.x jars of dbimport.
SVN URL of trunk:
https://agimatec-tools.googlecode.com/svn/trunk


## 2.2.8 (versions 2.2.x, Febr. 2010) ##
All 2.2.x versions of agimatec-tools support org.apache.poi version 3.1-beta2.
If you need dbimport with this poi version, checkout the branch and build the project or load one of 2.2.x jars of dbimport.
SVN URL of branch:
https://agimatec-tools.googlecode.com/svn/branches/BR-2.2.x


## 2.2.7 (16.12.2009) ##

doctype for dozer5.0 fixed
3rd party libs upgraded

  * API changes at ImportController.join() returns ImportControl instead of long
  * ImportControl(ler).description
  * ImportControl(ler).errorMessage

## 2.2.5 (24.03.2009) ##
  * fixed bug importing spreadsheet with missing cells (using cellNum from cell)
  * fixed exception handling for groovy scripts (forwards ImporterException properly)

## 2.2.2 (26.09.2008) ##
  * upgrade from groovy-1.0-jsr-06 to groovy-1.5.6

## 2.2 (18th june 2008) ##
  * dbimport: import native excel spreadsheets (using POI)
  * dbimport: minor changes on the API of class ImportController

## 2.1.3 (5th june 2008) ##
  * import csv, fixed-length, xml files
  * use java or groovy script to process data
  * optionally counts errors/rows, save status of imports in database