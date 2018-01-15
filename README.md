# CosmicWorkbench
A workbench that helps us count COSMIC function points.

The general use case is that developers use this tool to size software based on functional processes and the data groups used in those processes.

# To Run.

1. You require gradle installed somewhere
2. the "run" task of the gradle project will run the project
3. you will find the project on http://localhost:8080
4. The database structure will be revealed at http://localhost:8080/console. You need to connect to the database found in the following path: jdbc:h2:c:/opt/CosmicWorkbench/function (your project root, and then name of db is "function"
5. To understand the working state of the project, you need to look in the schema file and see which sql's are currently enabled, to tell you how much of the application works. 

Does this still work in 2018?
