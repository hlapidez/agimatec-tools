Prerequisite:
  * variable "jdbcConnection" contains a java.sql.Connection to the target database

# Import a csv-file (comma-separated plain text file) #

```
import com.agimatec.utility.fileimport.*;
import com.agimatec.utility.fileimport.groovy.*;
import java.io.*;
import groovy.sql.*;
import org.apache.commons.lang.WordUtils;

def jdbcConnection = tool.targetDatabase.connection;
def su = SqlUtil.forConnection(jdbcConnection);
su.defDate('Date', 'yyyy-MM-dd');
su.defSequence('id', 'SEQ_import');
ImportController controller = new ImportController(jdbcConnection, su, 'SEQ_import');
ImportControl imp = controller.join('zipcode-import'); // wait for other imports of that kind until finished...

def Sql db = new Sql(jdbcConnection);

String postcodeInsert = """
    INSERT INTO Postcode(postcode_id, zip, description, valid_from, country)
    values(${su.get('id')}, ?, ?, ?, ?)
""";


def spec = new LineImporterSpecGroovy({ processor ->
 def row = processor.currentRow;
 processor.log('row: ' + processor.rowCount + '; insert postcode: ' + row);
 controller.update(imp, processor.importer); // optional: write status into database
 if(db.executeUpdate (postcodeInsert,
    [row.PostalCode,
    WordUtils.capitalizeFully(row.PostalCodeDescription),
    su.timestamp('Date', row.DateFrom), row.CountryCode]) == 0) {
      throw new ImporterException("no row inserted: " + postcodeInsert, false);
    }
});

def importer = new Importer(spec);
db.execute 'DELETE FROM Postcode';
try {
    FileReader reader = new FileReader("setup/zipcodes.csv");
    importer.importFrom(reader);
} finally {
    controller.end(imp, importer);  // mark import as finished
}

```

## What happens here? ##
  * The file "zipcodes.csv" will be imported into a table "Postcode"
  * The LineImporterSpec contains a closure, that the importer will invoke for each row in the csv-file (except a header-row, if exists) to process the data.
  * The script uses plain Groovy-Sql APIs to execute the SQL statements with little assitance of the SqlUtil class used to convert the strings from the csv-file into proper java.sql.Date instances and to generate unique keys with a sequence named "SEQ\_import".

## Notes: ##
  * You can use an instance of class SqlUtil to provide some oracle/postgres independent constructs (e.g. handing of sequences) or datatype conversion (here: date). If you do not use SqlUtil, you can use the plain groovy APIs.
  * You can drop the creation of ImportController, the controller.update() call and the finally-block. The ImportController is not required basically. It uses a separate table "import\_control" to save the status of running imports and to block other imports until an import of the same name (here: "zipcode-import") has finished.

# Manage imports in database #
If you want to use an ImportController, you need to create the import\_control table (Postgres-syntax):

```
CREATE TABLE Import_Control (
    import_id BIGINT CONSTRAINT NN_import_id NOT NULL,
    start_time TIMESTAMP CONSTRAINT NN_start_time NOT NULL,
    end_time TIMESTAMP,
    status CHARACTER VARYING(40),
    import_name CHARACTER VARYING(250) CONSTRAINT NN_import_name NOT NULL,
    row_count INTEGER,
    error_count INTEGER,
    description CHARACTER VARYING(500),
    file_name CHARACTER VARYING(500),
    error_message CHARACTER VARYING(2000),
    CONSTRAINT Import_Control_pkey PRIMARY KEY (import_id)
);

```

(Oracle-syntax)
```
CREATE TABLE Import_Control (
    import_id INTEGER NOT NULL,
    start_time TIMESTAMP NOT NULL,
    end_time TIMESTAMP,
    status VARCHAR2(40),
    import_name VARCHAR2(250) NOT NULL,
    row_count INTEGER,
    error_count INTEGER,
    description VARCHAR2(500),
    file_name VARCHAR2(500),
    error_message VARCHAR2(2000),
    CONSTRAINT Import_Control_pkey PRIMARY KEY (import_id));
```

# Import XML file #

```
import com.agimatec.commons.config.*;
import com.agimatec.utility.fileimport.*;
import com.agimatec.utility.fileimport.groovy.*;
import java.io.*;
import groovy.sql.*;

...
def spec = new XmlSlurperSpec(
{ doc -> doc.user },
{ processor ->
  def user = processor.current;
  
  processor.log('element: ' + processor.rowCount + ' with: ' + user);
  def countryCode = su.trim(user.country.text());
  // ..
});
```

  * Main difference: XmlSlurperSpec instead of LineImporterSpecGroovy

# Import .xls (excel 97 format) file #

## Example 1 (Java) ##
```
  LineImporterSpecAutoFields spec = new LineImporterSpecAutoFields() {
    public void processRow(LineImportProcessor processor) throws ImporterException {
      ICell cell = (ICell)processor.getCurrentRow().get("the_header_column_name_here");
      Object value = cell.getValue();
    }
  };
  spec.setHeaderSpec(LineImporterSpec.Header.INDEX);
  spec.setHeaderLineIndex(5); // determine field names from row #5

  spec.setLineTokenizerFactory(new ExcelRowTokenizerFactory());

  Importer importer = new Importer(spec);
  importer.importFrom(new FileInputStream("Spreadsheet.xls"));

```

## Example 2 (Java) ##
since 2.5.13, using convenience class to read from sheets in a excel workbook

```
InputStream stream = new FileInputStream("workbook.xls");                       
ExcelRowTokenizerFactory factory = new SharedExcelRowReaderFactory();           
try {                                                                           
   ImporterSpec spec1 = new ExcelImporterSpec("SheetName1") { 
     protected void processRow(ExcelImportProcessor processor) {                   
      processor.getString("column title 1"));                                                           
      processor.getBoolean("column title 2"));                                                          
     }                                                                                                    
   };            
   spec1.setLineTokenizerFactory(factory);                                      
   Importer importer1 = new Importer(spec1);                                    
   importer1.importFrom(stream);                                                
                                                                                                                                                              
   ImporterSpec spec2 = new ExcelImporterSpec("SheetName2") { ... };            
   spec2.setLineTokenizerFactory(factory);                                      
   Importer importer2 = new Importer(spec2);                                    
   importer2.importFrom(stream);                                                
                                                                                
} finally {                                                                     
 stream.close();                                                                
}                                                                               
```

## Example 3 (Groovy) ##
```
  LineImporterSpecGroovy spec = new LineImporterSpecGroovy( { processor ->
    def cell = processor.currentRow['the_header_column_name_here'];
    def value = cell.value; // or: cell.stringValue
  );
  spec.setHeaderSpec(LineImporterSpec.Header.FIRST); // determine field names from row #1

  spec.setLineTokenizerFactory(new ExcelRowTokenizerFactory());

  Importer importer = new Importer(spec);
  importer.importFrom(new FileInputStream("Spreadsheet.xls"));

```

  * You can read all cell attributes (color, font, formular, comment) inside your processRow() method or groovy closure. (see class ExcelCell and refer to [POI-HSSF docs](http://poi.apache.org/hssf/index.html))


## Write Excel workbook ##
see class ExcelGenerator

Example of your custom subclass:
```
public class MyWorkbookGenerator extends ExcelGenerator {
     final MyData input;
    // Custom styles
    String STYLE_UNDERLINE_HEADER = "underlineHeader";
    protected HSSFSheet sheet;
    protected HSSFRow row;

     public WorkbookGenerator(MyData input) {
        this.input = input;
    }

   @Override
    protected void initStyles() {
        super.initStyles();    // call super!
        HSSFCellStyle style = getStyles().boldHeader();
        style.getFont(wb).setUnderline(Font.U_SINGLE);
        getStyles().put(STYLE_UNDERLINE_HEADER, style);

        // .. more custom styles to create+cache here
   } 


    @Override
    protected void generateSheets() {
        generateMySheet1(); // ... custom logic to create the sheets with data
   } 

    private void generateMySheet1() {
        sheet = wb.createSheet("MySheetName 1");
        int rowNum = 0;
        row = sheet.createRow(rowNum++);
        createCell(row, 0, "My Sheet Title", style(STYLE_boldTitle));
        createCell(row, 10, "My Legend", style(STYLE_boldHeader));
        createHeaders(sheet.createRow(rowNum++), 0, style(STYLE_boldHeader),
          "First Name", "Last Name", "MyHeader3"
        );
        sheet.getRow(rowNum - 1).getCell(0).setCellStyle(style(STYLE_UNDERLINE_HEADER));
         // etc...

         for (MyDataEntry each : input.getEntries()) {
            row = sheet.createRow(rowNum++);
            int colNum = 0;
            createCell(row, colNum++, each.getFirstName()); 
            createCell(row, colNum++, each.getLastName()); 
            // etc...
         }
         // etc...
    }
}

```