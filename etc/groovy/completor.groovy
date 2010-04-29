// Item types
interface Item {
    final def ALL_LANGS = "all"
    abstract def getValue () { value }
    abstract def getScope()
}

class LabelItem implements Item {   
    private final def scope
    private final def value
    LabelItem(labelText) { this(labelText, null) }
    LabelItem(labelText, scope) { 
        this.value = "# ${labelText}"
        this.scope = scope 
    }
    def getScope() { scope ?: ALL_LANGS }
    def getValue() { value }
}

class PropertyItem implements Item {
    private final def scope
    private final def value
    PropertyItem(key, value, scope) { 
        this.value = "${key}=${value}" 
        this.scope = scope 
    }
    def getScope() { scope }
    def getValue() { value }
}

class EmptyItem implements Item {
    def getScope() { ALL_LANGS }
    def getValue() { "" }
}/**/

// Csv parsing
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

// Collector
class PropertyCollector {
    private def langHeaderPattern = /(?m)^([A-Z]{2})\s*\(.*\)$/
    private def defaultLang = ""
    private def allowedLangs = []
    private def parsedLangs = [:]
    private def contexts = [:]
    private def currentContext = null
    
    PropertyCollector(defaultLang, allowedLangs) {
        this.defaultLang = defaultLang
        this.allowedLangs = allowedLangs*.toLowerCase()
    }
    def parseHeader(rowNo, rowHeaders) {
        def index = 0
        println "${rowNo}: row headers: ${rowHeaders}"
        rowHeaders.each {
            (it =~ langHeaderPattern).each {
                def lang = it[1].toLowerCase()
                if (allowedLangs.contains(lang)) {
                    parsedLangs[index] = lang
                }
            }
            index++
        }
        println "\t${rowNo}: parsed langs: ${parsedLangs}"
    }
    def switchContext(rowNo, contextName) {
        println "\t${rowNo}: switching context to: ${contextName}"
        if (!contexts[contextName]) {
            contexts[contextName] = [:]
            parsedLangs.values().each { lang ->
                contexts[contextName][lang] = []
            }
        }
        currentContext = contextName
    }
    def addPropertyItem(rowNo, key, values) {
        // println "${rowNo}: adding PropertyItem: ${key}, ${values}"
        parsedLangs.keySet().each { index ->
            if (values[index]) {
                addItem(new PropertyItem(key, values[index], parsedLangs[index]))
            } else {
                def cause = "ERROR property with ${key} does not have value in ${parsedLangs[index]} language"
                println cause
                addItem(new LabelItem(cause, parsedLangs[index]))
            }
        }
    }
    def addLabelItem(rowNo, labelText) {
        // println "${rowNo}: adding LabelItem: ${labelText}"
        addItem(new LabelItem(labelText))
    }
    def addEmptyItem(rowNo) {
        // println "${rowNo}: adding EmptyItem"
        addItem(new EmptyItem())
    }
    private def addItem(item) {
        if (currentContext == null) { 
            throw new RuntimeException("Context can not be null")
        }
        if (item.scope == Item.ALL_LANGS) {
            parsedLangs.values().each { lang ->
                contexts[currentContext][lang].add(item)
            }
        } else if (allowedLangs.contains(item.scope)) {
            contexts[currentContext][item.scope].add(item)
        }
    }
    def getResult(closure) {
        contexts.each { contextName, langs ->
            langs.each { lang, props ->
                if (lang == defaultLang) {
                    closure(contextName, null, props)
                }
                closure(contextName, lang, props)
            }
        }
    }
}

// Input parameters
def inputFilename = "c:/pub/i18n/testFinal.csv"
def outputPath = "c:/pub/i18n/"
def keyExceptions = ["from", "to", "of", "up", "down", "left", "right"]
def defaultLang = "en"
def langsToInclude = ["en", "de", "sv", "nl"]

println "included langs will be ${langsToInclude}"
println "default lang will be ${defaultLang}"
def collector = new PropertyCollector(defaultLang, langsToInclude)

// Parsing of input
println "parsing csv file ${inputFilename}"
use(CsvParser.class) {
    File file = new File(inputFilename)  
    file.parseCsv { index, row -> 
        if (index == 1) {
            collector.parseHeader(index, row[1..-1])
        } else if (row[0]) {
            if ((row[0].contains(".") && !row[0].contains("...")) || keyExceptions.contains(row[0])) {
                collector.addPropertyItem(index, row[0], row[1] ? row[1..-1] : [])
            } else {
                if (row[1]) {
                    collector.switchContext(index, row[1])
                }
                collector.addLabelItem(index, row[0])
            }
        } else {
            collector.addEmptyItem(index)
        }
    }  
}

// Flush output
println "saving parsed properties"
collector.getResult { contextName, lang, properties ->
    def outputFilename = outputPath + contextName + (lang ? "_" + lang : "") + ".properties"
    println "\tsaving file ${outputFilename}"
    File out = new File(outputFilename)
    out.delete()
    properties.each {
        out << "${it.value}\n"
    }
}

return