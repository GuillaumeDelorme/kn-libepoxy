import java.io.File

// Reading args
if (args.size != 1) {
    println("The script require only one argument : path to the 'gl_generated.h' file")
    System.exit(1)
}

val headerFile = File(args[0])

if (!headerFile.exists()) {
    println("Unable to find the 'gl_generated.h' file")
    System.exit(1)
}

// Generating epoxy.def file
println("Creating file 'epoxy.def'")
File("epoxy.def").printWriter().use { out ->
    out.println("depends = posix")
    out.println("package = epoxy")
    out.println("headers = epoxy/gl.h")
    out.println("headerFilter = epoxy/**")
    out.println("linkerOpts = -lepoxy")
    out.println( "\n---\n")

    // Adding OpenGL functions from header file
    headerFile
            .readLines()
            .filter { it.startsWith("EPOXY_PUBLIC ") }
            .forEach {
                val returnType = it.split(" ")[1]

                val epoxyFunction = it.substringAfter("(EPOXY_CALLSPEC *").substringBefore(")")
                val glFunction = epoxyFunction.replaceFirst("epoxy_", "")

                var params = it.substringAfter(")").replace(";", "")
                params = when (params) {
                    "(void)" -> "()"
                    else -> params
                }

                val paramsWithoutTypes = when (params) {
                    "()" -> "()"
                    else -> params
                            .split(", ")
                            .map { it.split(" ").last() }
                            .joinToString(prefix = "(")
                }

                val returnKeyword = when(returnType) {
                    "void" -> ""
                    else -> "return "
                }

                out.println("#undef $glFunction")
                out.println("static inline $returnType $glFunction$params {")
                out.println("\t$returnKeyword$epoxyFunction$paramsWithoutTypes;")
                out.println("}\n")

                println("Function '$glFunction' added")
            }
}

println("File successfully generated")