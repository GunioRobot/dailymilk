def cli = new CliBuilder(usage: 'groovy PropertiesUsage.groovy [-hvf] ["outputfile"]')  
cli.h(longOpt: 'help',     'usage information',    required: false)  
cli.n(longOpt: 'unused',   'only unused props.',   required: false)
cli.u(longOpt: 'used',     'only used props.',     required: false)
cli.v(longOpt: 'verbose',  'verbose mode',         required: false)  
cli.f(longOpt: 'file',     'output to file',       required: false,     args: 1)  
OptionAccessor opt = cli.parse(args)  

// Print usage if -h, --help, or no argument is given  
if (!opt || opt.h ) { cli.usage() }
def verboseMode = true //opt.v ?: true
def outputFilename = opt.f ?: "c:/pub/unused.properties"
def reportUnused = true // opt.n && !opt.u ?: false
def onlyUsed = false // opt.u && !opt.n ?: false

// Parameters of script
def rootDirs = ["d:/eclipse_workspace/projekte/xrailLongterm/src/action", "d:/eclipse_workspace/projekte/xrailLongterm/src/model", "d:/eclipse_workspace/projekte/xrailLongterm/view"]
def resourceBundles = ["labels", "calculation", "combination", "importer", "Resource"]

// Load all keys from resource bundles
def keysToFind = new TreeSet<String>()
resourceBundles.each { bundleName -> 
    def bundle = ResourceBundle.getBundle(bundleName)
    keysToFind.addAll(Collections.list(bundle.getKeys()))
}
def keysString = keysToFind.join('|')
def keysPattern = /(?m)^.*($keysString).*$/
def keysCount = [:]

def filenamePattern = /(?m)^.*[^(\.svn)]*(\.xml|\.java|\.xhtml)$/

// Collect all files for match
if (reportUnused || onlyUsed) {
    rootDirs.each { dirname ->
        new File(dirname).eachFileRecurse() { file ->
            def filename = file.getName()
            if (!file.isDirectory() && (filename =~ filenamePattern)) {
                if (verboseMode) {
                    println "Parsing: ${file.getPath()}"
                }
                (file.text =~ keysPattern).each { line, matchedKey -> 
                    keysCount[matchedKey] = (keysCount[matchedKey] ?: 0) + 1
                }
            }
        }
    }
    
    if (reportUnused) { // Report not used keys
        keysToFind.removeAll(keysCount.keySet())
    } else { // Report found keys
        keysToFind = keysCount.keySet()
    }
}

// Write result
def outputClosure = { println it }
if (outputFilename) {
    File output = new File(outputFilename)
    output.delete()
    outputClosure = { output << "${it}\n" }
}
keysToFind.each(outputClosure)

return