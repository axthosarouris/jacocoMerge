## Gradle plugin demo where the functional tests have to access the testing project classpath

### Functionality description:
This plugin accepts a configuration of the form belwo and merges the Jacoco test reports of the 
subprojects of the specified project in one aggregated report.
```
jacocoMergeReport {
projectName = "testProject01"
}
```

### Build plugin:
./gradlew build

### Structure details:

#### Folder `testProject`
This is the testing project used for testing the functionality of the plugin. In the current setup,
it is a module in the project. It can use the plugin code direcly from the buildSrc folder.


#### Folder `buildSrc`
* The  `main` folder contains the main implementation logic
* The `test` folder contains Unit tests for the various modules of the plugin

###### Notes
1. Debugging the buildSrc tests can only be done by performing remote debugging on any gradle command,
as `buildSrc` is not a proper module of the class. 
2. `buildSrc` builds on every gradle command if the are changes in the `buildSrc` code.


#### Folder `functionaltesting` 

The `functionaltesting` folder contains tests verify the functionality of the plugin by examining
the result of a gradle action on the `testProject`.

###### Notes

1. The`functionalTesting` code can probably use a GradleRunner in its tests via the method 
`GradleRunner.create().withPluginClasspath()`, and therefore it has all 
the abilities with the `functionalTest` folder in the 
[default plugin setup](https://github.com/axthosarouris/gradle-plugin-no-test-project-dependencies)
but it has also the ability to run functionality tests on the result of building the project itself, 
i.e. we can test the result of executing the task `gradle testProject:<task>` without having to use 
a GradleRunner. This can be helpful if the `testProject` is big and complex as we can make use of 
parallel builds. 

2. Both  `functionalTesting` and `testingProject` are basically  modules in the same project and therefore 
`functionalTesting` can have easy access to the classpath of the `testingProject`. For example, in
this project we need access to the classes of the testingProject in order to list the methods of the classes.

3. Although the `testProject` is a module in the plugin project, it can be easily turned into 
  an "independent" project for running with the GradleRunner, by injecting a `settings.gradle` file
  as we do in the [default plugin setup](https://github.com/axthosarouris/gradle-plugin-no-test-project-dependencies).

###### Notes

1. It is important to note that in the `test` folder we cannot instantiate the Tasks that we create, 
   and therefore we can only test logic that is connected to the project structure, i.e., whatever
   one can define withing the files `build.gradle` and `settings.gradle`. If the plugin task interacts
   with some project files (of the testing project), we can test such interactions with the
   *functional tests*.

2. Given the current setup, it is not possible for the functional tests to have access to the
classpath of the `buildSrc` main` `test` folder. That is to say, the *functional tests* observe only
the results of the build that is run 



#### General notes:

The greatest advantage of this structure against the default, is that we have access to the 
testing-project classpath without syncing problems. However, it comes with the price that it is harder
to debug the actual plugin and that we do not know yet how to use the GradleRunner in the project
in order to test the functionality using different builds.

Although this structure appears to have some more possibilities than the default, it can be harder 
to work with and if access to the testing-project classpath is not necessary, then it is advised 
to start with the [default plugin setup](https://github.com/axthosarouris/gradle-plugin-no-test-project-dependencies).
Conversion to this structure from teh default setup is easy.



