Dexter
==================


Dexter is an Android library that simplifies the process of requesting permissions at runtime.

Android Marshmallow includes a new functionality to let users grant or deny permissions when running an app instead of granting them all when installing it. This approach gives the user more control over applications but requires developers to add lots of code to support it.

The official API is heavily coupled with the ``Activity`` class.
Dexter frees your permission code from your activities and lets you write that logic anywhere you want.


Screenshots
-----------

![Demo screenshot][1]

Usage
-----

To start using the library you just need to initialize Dexter with a ``Context``, preferably your ``Application`` as it won't be destroyed during your app lifetime:

```java
public MyApplication extends Application {
	@Override public void onCreate() {
		super.onCreate();
		Dexter.initialize(context);
	}
}
```

Once the library is initialized you can start checking permissions at will. For each permission, register a ``PermissionListener`` implementation to receive the state of the request:
```java
Dexter.checkPermission(Manifest.permission.CAMERA, new PermissionListener() {
	@Override public void onPermissionGranted(String permission) {/* ... */}
	@Override public void onPermissionDenied(String permission) {/* ... */}
	@Override public void onPermissionRationaleShouldBeShown(String permission, PermissionToken token) {/* ... */}
});
```

To make your life easier we offer some ``PermissionListener`` implementations to perform recurrent actions:
* ``EmptyPermissionListener`` to make it easier to implement only the methods you want.
* ``DialogOnDeniedPermissionListener`` to show a configurable dialog whenever the user rejects a permission request.
* ``SnackbarOnDeniedPermissionListener`` to show a snackbar message whenever the user rejects a permission request.
* ``MultiPermissionListener`` to compound multiple listeners into one.


**IMPORTANT**: Remember to follow [Google design patterns] [2] to make your application as user-friendly as possible.

Add it to your project
----------------------

Include the library in your ``build.gradle``

```groovy
dependencies{
    compile 'com.karumi:dexter:1.0.0'
}
```

or to your ``pom.xml`` if you are using Maven

```xml
<dependency>
    <groupId>com.karumi</groupId>
    <artifactId>dexter</artifactId>
    <version>1.0.0</version>
    <type>aar</type>
</dependency>

```

Do you want to contribute?
--------------------------

Feel free to add any useful feature to the library, we will be glad to improve it with your help.

Libraries used in this project
------------------------------

* [JUnit] [3]
* [Butterknife] [4]

License
-------

    Copyright 2015 Karumi

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[1]: ./art/example.gif
[2]: http://www.google.es/design/spec/patterns/permissions.html
[3]: https://github.com/junit-team/junit
[4]: https://github.com/JakeWharton/butterknife