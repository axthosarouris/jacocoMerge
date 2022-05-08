## Gradle plugin for creating a project code coverage report by merging all Jacoco code-coverage
reports of the different project modules 

### Functionality description:
This plugin accepts a configuration of the form below and merges the Jacoco test reports of the 
subprojects of the specified project or module in one aggregated report. The report will be located 
in the build folder of this project/module.

```
jacocoMergeReport {
projectName = "<project/module name>"
}
```

### Build plugin:
./gradlew build

### Structure details:

#### Folder `testProject`
This is the testing project used for testing the functionality of the plugin. In the current setup,
it is a module in the project. It can use the plugin code directly from the buildSrc folder.


#### Folder `buildSrc`
* The  `main` folder contains the main implementation logic
* The `test` folder contains Unit tests for the various modules of the plugin

###### Testing/Debugging
1. Debugging the buildSrc tests can only be done by performing remote debugging on any gradle command,
as `buildSrc` is not a proper module of the class. 
2. `buildSrc` builds on every gradle command if the are changes in the `buildSrc` code.
3. To perform the debugging you  should kill al gradle daemons and 
     enable the following lines  in the `gradle.properties` file.   
    ```
        #org.gradle.daemon=false
        #org.gradle.debug=true
   ```
 
 



