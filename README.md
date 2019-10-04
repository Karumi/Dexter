![Karumi logo][karumilogo] Dexter [![Build Status](https://travis-ci.org/Karumi/Dexter.svg?branch=master)](https://travis-ci.org/Karumi/Dexter) [![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.karumi/dexter/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.karumi/dexter) [![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-Dexter-green.svg?style=true)](https://android-arsenal.com/details/1/2804)
======


Dexter is an Android library that simplifies the process of requesting permissions at runtime.

Android Marshmallow includes a new functionality to let users grant or deny permissions when running an app instead of granting them all when installing it. This approach gives the user more control over applications but requires developers to add lots of code to support it.

The official API is heavily coupled with the ``Activity`` class.
Dexter frees your permission code from your activities and lets you write that logic anywhere you want.


Screenshots
-----------

![Demo screenshot][1]

Usage
-----

### Dependency

Include the library in your ``build.gradle``

```groovy
dependencies{
    implementation 'com.karumi:dexter:6.0.0'
}
```


To start using the library you just need to call `Dexter` with a valid `Activity`:

```java
public MyActivity extends Activity {
	@Override public void onCreate() {
		super.onCreate();
		Dexter.withActivity(activity)
			.withPermission(permission)
			.withListener(listener)
			.check();
	}
}
```

### Single permission
For each permission, register a ``PermissionListener`` implementation to receive the state of the request:

```java
Dexter.withActivity(this)
	.withPermission(Manifest.permission.CAMERA)
	.withListener(new PermissionListener() {
		@Override public void onPermissionGranted(PermissionGrantedResponse response) {/* ... */}
		@Override public void onPermissionDenied(PermissionDeniedResponse response) {/* ... */}
		@Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {/* ... */}
	}).check();
```

To make your life easier we offer some ``PermissionListener`` implementations to perform recurrent actions:

* ``BasePermissionListener`` to make it easier to implement only the methods you want. Keep in mind that you should not call `super` methods when overriding them.
* ``DialogOnDeniedPermissionListener`` to show a configurable dialog whenever the user rejects a permission request:

```java
PermissionListener dialogPermissionListener =
	DialogOnDeniedPermissionListener.Builder
		.withContext(context)
		.withTitle("Camera permission")
		.withMessage("Camera permission is needed to take pictures of your cat")
		.withButtonText(android.R.string.ok)
		.withIcon(R.mipmap.my_icon)
		.build();
```

* ``SnackbarOnDeniedPermissionListener`` to show a snackbar message whenever the user rejects a permission request:

```java
PermissionListener snackbarPermissionListener =
	SnackbarOnDeniedPermissionListener.Builder
		.with(view, "Camera access is needed to take pictures of your dog")
		.withOpenSettingsButton("Settings")
        .withCallback(new Snackbar.Callback() {
            @Override
            public void onShown(Snackbar snackbar) {
                // Event handler for when the given Snackbar is visible
            }
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                // Event handler for when the given Snackbar has been dismissed
            }
        }).build();
```

* ``CompositePermissionListener`` to compound multiple listeners into one:

```java
PermissionListener snackbarPermissionListener = /*...*/;
PermissionListener dialogPermissionListener = /*...*/;
PermissionListener compositePermissionListener = new CompositePermissionListener(snackbarPermissionListener, dialogPermissionListener, /*...*/);
```

### Multiple permissions
If you want to request multiple permissions you just need to call `withPermissions` and register an implementation of ``MultiplePermissionsListener``:

```java
Dexter.withActivity(this)
	.withPermissions(
		Manifest.permission.CAMERA,
		Manifest.permission.READ_CONTACTS,
		Manifest.permission.RECORD_AUDIO
	).withListener(new MultiplePermissionsListener() {
	    @Override public void onPermissionsChecked(MultiplePermissionsReport report) {/* ... */}
	    @Override public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {/* ... */}
	}).check();
```

The ``MultiplePermissionsReport`` contains all the details of the permission request like the list of denied/granted permissions or utility methods like ``areAllPermissionsGranted`` and ``isAnyPermissionPermanentlyDenied``.

As with the single permission listener, there are also some useful implementations for recurring patterns:

* ``BaseMultiplePermissionsListener`` to make it easier to implement only the methods you want. Keep in mind that you should not call `super` methods when overriding them.
* ``DialogOnAnyDeniedMultiplePermissionsListener`` to show a configurable dialog whenever the user rejects at least one permission:

```java
MultiplePermissionsListener dialogMultiplePermissionsListener =
	DialogOnAnyDeniedMultiplePermissionsListener.Builder
		.withContext(context)
		.withTitle("Camera & audio permission")
		.withMessage("Both camera and audio permission are needed to take pictures of your cat")
		.withButtonText(android.R.string.ok)
		.withIcon(R.mipmap.my_icon)
		.build();
```

* ``SnackbarOnAnyDeniedMultiplePermissionsListener`` to show a snackbar message whenever the user rejects any of the requested permissions:

```java
MultiplePermissionsListener snackbarMultiplePermissionsListener =
	SnackbarOnAnyDeniedMultiplePermissionsListener.Builder
		.with(view, "Camera and audio access is needed to take pictures of your dog")
		.withOpenSettingsButton("Settings")
        .withCallback(new Snackbar.Callback() {
            @Override
            public void onShown(Snackbar snackbar) {
                // Event handler for when the given Snackbar is visible
            }
            @Override
            public void onDismissed(Snackbar snackbar, int event) {
                // Event handler for when the given Snackbar has been dismissed
            }
        })
		.build();
```

* ``CompositePermissionListener`` to compound multiple listeners into one:

```java
MultiplePermissionsListener snackbarMultiplePermissionsListener = /*...*/;
MultiplePermissionsListener dialogMultiplePermissionsListener = /*...*/;
MultiplePermissionsListener compositePermissionsListener = new CompositeMultiplePermissionsListener(snackbarMultiplePermissionsListener, dialogMultiplePermissionsListener, /*...*/);
```

### Handling listener threads
If you want to receive permission listener callbacks on the same thread that fired the permission request, you just need to call ``onSameThread`` before checking for permissions:

```java
Dexter.withActivity(activity)
	.withPermission(permission)
	.withListener(listener)
	.onSameThread()
	.check();
```

### Showing a rationale
Android will notify you when you are requesting a permission that needs an additional explanation for its usage, either because it is considered dangerous, or because the user has already declined that permission once.

Dexter will call the method ``onPermissionRationaleShouldBeShown`` implemented in your listener with a ``PermissionToken``. **It's important to keep in mind that the request process will pause until the token is used**, therefore, you won't be able to call Dexter again or request any other permissions if the token has not been used.

The most simple implementation of your ``onPermissionRationaleShouldBeShown`` method could be:

```java
@Override public void onPermissionRationaleShouldBeShown(PermissionRequest permission, PermissionToken token) {
	token.continuePermissionRequest();
}
```

### Error handling
If you think there is an error in your Dexter integration, just register a `PermissionRequestErrorListener` when calling Dexter:

```java
Dexter.withActivity(activity)
	.withPermission(permission)
	.withListener(listener)
	.withErrorListener(new PermissionRequestErrorListener() {
		@Override public void onError(DexterError error) {
			Log.e("Dexter", "There was an error: " + error.toString());
		}
	}).check();
```

The library will notify you when something bad happens. In general, it is a good practice to, at least, log every error Dexter may throw but is up to you, the developer, to do that.

**IMPORTANT**: Remember to follow the [Google design guidelines][2] to make your application as user-friendly as possible.

### Permission dialog not being shown

If you are using the ``MultiplePermissionsListener`` and you don't see the permission dialog the second time the permission is checked review your configuration. Keep in mind you need to let Dexter know the rationale you can show was closed or not by calling ``token?.continuePermissionRequest()``. If you don't do this, the next time the permission is requested, the OS dialog asking for this permission won't be shown. You can find more information about this in [here](https://github.com/Karumi/Dexter/issues/105). This is an example of how a multiple permission request should be implemented:

```kotlin
button.setOnClickListener {
    Dexter.withActivity(this@MainActivity)
                        .withPermissions(
                            Manifest.permission.ACCESS_COARSE_LOCATION
                            ,Manifest.permission.ACCESS_FINE_LOCATION)
                        .withListener(object: MultiplePermissionsListener {
                            override fun onPermissionsChecked(report: MultiplePermissionsReport?) {
                                report?.let {
                                    if(report.areAllPermissionsGranted()){
                                        toast("OK")
                                    }
                                }
                            }
                            override fun onPermissionRationaleShouldBeShown(
                                permissions: MutableList<PermissionRequest>?,
                                token: PermissionToken?
                            ) {
                                // Remember to invoke this method when the custom rationale is closed
                                // or just by default if you don't want to use any custom rationale.
                                token?.continuePermissionRequest()
                            }
                        })
                        .withErrorListener {
                            toast(it.name)
                        }
                        .check()
}
```

Caveats
-------
* For permissions that did not exist before API Level 16, you should check the OS version and use *ContextCompat.checkSelfPermission*. See [You Cannot Hold Non-Existent Permissions](https://commonsware.com/blog/2015/11/09/you-cannot-hold-nonexistent-permissions.html).
* Don't call Dexter from an Activity with the flag `noHistory` enabled. When a permission is requested, Dexter creates its own Activity internally and pushes it into the stack causing the original Activity to be dismissed.
* Permissions `SYSTEM_ALERT_WINDOW` and `WRITE_SETTINGS` are considered special by Android. Dexter doesn't handle those and you'll need to request them in the old fashioned way.

# Contributors

Thank you all for your work!

| [<img src="https://avatars1.githubusercontent.com/u/3494156?v=4" width="100px;"/><br /><sub><b>Carlos Morera de la Chica</b></sub>](https://github.com/CarlosMChica) | [<img src="https://avatars0.githubusercontent.com/u/237122?v=4" width="100px;"/><br /><sub><b>Alex Florescu</b></sub>](https://github.com/anothem) | [<img src="https://avatars3.githubusercontent.com/u/416941?v=4" width="100px;"/><br /><sub><b>Pedro Veloso</b></sub>](https://github.com/pedronveloso) | [<img src="https://avatars3.githubusercontent.com/u/1636897?v=4" width="100px;"/><br /><sub><b>Dion Segijn</b></sub>](https://github.com/DanielMartinus) | [<img src="https://avatars0.githubusercontent.com/u/837104?v=4" width="100px;"/><br /><sub><b>Christian Panadero</b></sub>](https://github.com/PaNaVTEC) |
| :---: | :---: | :---: | :---: | :---: |
| [<img src="https://avatars3.githubusercontent.com/u/6309101?v=4" width="100px;"/><br /><sub><b>Vignesh</b></sub>](https://github.com/VigneshPeriasami) | [<img src="https://avatars0.githubusercontent.com/u/5009609?v=4" width="100px;"/><br /><sub><b>Andy French</b></sub>](https://github.com/AndyFrench) | [<img src="https://avatars2.githubusercontent.com/u/887462?v=4" width="100px;"/><br /><sub><b>Bernat Borrás Paronella</b></sub>](https://github.com/alorma) | [<img src="https://avatars0.githubusercontent.com/u/21121410?v=4" width="100px;"/><br /><sub><b>Bastien Paul</b></sub>](https://github.com/bastienpaulfr) | [<img src="https://avatars2.githubusercontent.com/u/4190298?v=4" width="100px;"/><br /><sub><b>Bas Broek</b></sub>](https://github.com/BasThomas) |
| [<img src="https://avatars2.githubusercontent.com/u/6371716?v=4" width="100px;"/><br /><sub><b>Bartek Lipinski</b></sub>](https://github.com/blipinsk) | [<img src="https://avatars0.githubusercontent.com/u/4202457?v=4" width="100px;"/><br /><sub><b>emmano</b></sub>](https://github.com/emmano) | [<img src="https://avatars1.githubusercontent.com/u/7110368?v=4" width="100px;"/><br /><sub><b>Konrad Morawski</b></sub>](https://github.com/Konrad-Morawski) | [<img src="https://avatars3.githubusercontent.com/u/18151375?v=4" width="100px;"/><br /><sub><b>Hrant Alaverdyan</b></sub>](https://github.com/rid-hrant) | [<img src="https://avatars2.githubusercontent.com/u/1730320?v=4" width="100px;"/><br /><sub><b>Carla</b></sub>](https://github.com/iriberri)
| [<img src="https://avatars1.githubusercontent.com/u/3470701?v=4" width="100px;"/><br /><sub><b>Pavel Stepanov</b></sub>](https://github.com/stefan-nsk) | [<img src="https://avatars2.githubusercontent.com/u/16154410?v=4" width="100px;"/><br /><sub><b>Emmett Wilson</b></sub>](https://github.com/EmmettWilson) | [<img src="https://avatars0.githubusercontent.com/u/16763485?v=4" width="100px;"/><br /><sub><b>Max</b></sub>](https://github.com/TheMaxCoder) | [<img src="https://avatars3.githubusercontent.com/u/18224621?v=4" width="100px;"/><br /><sub><b>Al B.</b></sub>](https://github.com/albodelu) | [<img src="https://avatars0.githubusercontent.com/u/578021?v=4" width="100px;"/><br /><sub><b>Vladislav Bauer</b></sub>](https://github.com/vbauer)
| [<img src="https://avatars2.githubusercontent.com/u/4047514?v=4" width="100px;"/><br /><sub><b>Jc Miñarro</b></sub>](https://github.com/JcMinarro) | [<img src="https://avatars0.githubusercontent.com/u/944957?v=4" width="100px;"/><br /><sub><b>handrenliang</b></sub>](https://github.com/handrenliang) | [<img src="https://avatars3.githubusercontent.com/u/19774257?v=4" width="100px;"/><br /><sub><b>jcruzsousa</b></sub>](https://github.com/jcruzsousa) | [<img src="https://avatars0.githubusercontent.com/u/18180559?v=4" width="100px;"/><br /><sub><b>lzdon</b></sub>](https://github.com/lzdon)

Do you want to contribute?
--------------------------

Feel free to add any useful feature to the library, we will be glad to improve it with your help.

Keep in mind that your PRs **must** be validated by Travis-CI. Please, run a local build with ``./gradlew checkstyle build test`` before submiting your code.


Libraries used in this project
------------------------------

* [Butterknife][3]
* [JUnit][4]
* [Mockito][5]

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

[1]: ./art/sample.gif
[2]: http://www.google.es/design/spec/patterns/permissions.html
[3]: https://github.com/JakeWharton/butterknife
[4]: https://github.com/junit-team/junit
[5]: https://github.com/mockito/mockito
[karumilogo]: https://cloud.githubusercontent.com/assets/858090/11626547/e5a1dc66-9ce3-11e5-908d-537e07e82090.png
