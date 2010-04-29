def separator = ";"
def inputFilename = "c:/pub/new.properties"
def outputFilename = "c:/pub/new.csv"

def resourceBundles = ["labels", "calculation", "combination", "importer", "Resource"]

def keys = new TreeSet<String>()
File input = new File(inputFilename)
input.eachLine { resourceKey -> keys.add(resourceKey.trim()) }

def results = [:]
resourceBundles.each { bundleName ->
    def bundle = ResourceBundle.getBundle(bundleName)
    keys.each { key ->
        println key
        if (!results.containsKey(key)) {
            try {
                def value = bundle.getString(key)
                results.put(key,value)
                println "key: ${key} = ${value} found in ${bundleName}"
            } catch (MissingResourceException e) { println e.getMessage() }
        }
    }
}

File output = new File(outputFilename)
output.delete()
results.each { entry -> 
    def value = entry.value
    if (value.contains(separator)) {
        value = "'${value}'"
    }
    output << "${entry.key}${separator}${entry.value}\n" 
}