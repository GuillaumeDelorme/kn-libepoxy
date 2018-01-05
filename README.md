Small script to generate Kotlin/Native ".def" file for Epoxy (https://github.com/anholt/libepoxy).

To launch the script :
```sh
kotlinc -script gen_libepoxy_def.kts -- /path/to/epoxy/gl_generated.h
```

Add the generated `epoxy.def` file in the `src/main/c_interop` of your project and configure your build script to use the library.

The script was tested with Epoxy 1.4.3.