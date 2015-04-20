# Blackboard Mobile Android 

This app is to be used with the blackboard mobile building block. 

## Configuration

Update file `src\main\java\edu\gvsu\bbmobile\MyBB\MyGlobal.java` with correct values for your blackboard installation.

`baseUrl` -- URL of your institutions blackboard.
`cookieDomain` -- This is the domain your blackboard will use when setting cookies.


`loginSuffix` -- String value that when appended to `baseUrl` would be the page requested when the user clicks login to your blackboard page.

**Note** this has not been tested with any other authentication other than using the standard blackboard login page.  





