
Thymeleaf Layout Dialect benchmarking web app
=============================================

A basic web application to test the memory profile of the
[Thymeleaf Layout Dialect](https://github.com/ultraq/thymeleaf-layout-dialect).

To execute the benchmark, use `./gradlew benchmark` from the command line.  This
will start the test application, then execute the JMeter test plan over it.

Once completed, JMeter's HTML reports can be found in `build/reports/(dialect-version)/(date-time-of-run)`.

To include YourKit profiling for memory snapshots, then the agent needs to be
loaded with the web application via the `-agentpath` VM argument.  eg: on MacOS:

`-agentpath:"/Applications/YourKit Java Profiler 2015.app/Contents/Resources/bin/mac/libyjpagent.jnilib"`
