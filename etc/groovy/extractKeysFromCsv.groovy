def separator = ";"
def inputFilename = "c:/pub/test.csv"
def outputFilename = "c:/pub/translated.properties"

class CsvParser {  
    static def parseCsv(file,closure) {  
        def lineCount = 0  
        file.eachLine() { line ->  
            def row = line.tokenize(";")  
            lineCount++  
            closure(lineCount, row)  
        }  
    }  
}

def keys = new TreeSet<String>();
use(CsvParser.class) {  
    File file = new File(inputFilename)  
    file.parseCsv { index, row ->  
        if (row[0] && row[0].contains(".")) {
           keys.add(row[0])
        }
    }  
}
File out = new File(outputFilename)
keys.each { out << "${it}\n" }