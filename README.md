# WebJavac
This library is a javac applet for browsers.

## Usage
To implement WebJavac on your own site, you have to do many things:

* Edit the security checker in `org.javascool.webjavac.Gateway`
   In the function `init()` change the code to :

```java
if(!getCodeBase().getHost().equals("example.com")){
            appletLocked=true;
}
```

   Don't forget to replace `example.com` by your own website

* Compile the jar (An big step):
    To create the jar for the library, use the Makefile (use Cygwin on Windows).
    You have to put some environment varaibles:
    * JDK_BIN : The root of your JDK instalation (the bin folder)
    * sign_key: The keystore's key used to sign the applet
    To build the jar and sign it, execute :
    ```make webjavac.jar```

* Add libs to your HTML page :
```html
<script src="http://ajax.googleapis.com/ajax/libs/jquery/1.7/jquery.min.js"></script>
<script src="jquery.webjavac.js"></script>
```
    Copy jquery.webjavac.js and webjavac.jar beside your html file

* Run the plugin :
```js
$(document).ready(function(){
    $.webjavac({
            jar:"webjavac.jar", // Location of the JAR (If you don't write it the default location will be polyfilewriter.jar)
            id:"PolyFileWriter" // The applet id (If you don't write it the default location will be a random UUID)
    });
});
```

## Code
The code has got tow sides, one part is written in Java and the other as a jQuery plugin.

For the part in Java, there is two class :

* org.javascool.core: Package written for Java's Cool 4 with all Javac command simplifed
* org.javascool.webjavac.Gateway : An applet class to talk with JavaScript

For the JS part, it's a simple JQuery extension

### Libraries
We use Java's Cool FileManager Lib, JSon-Simple and tools.jar stuff from Sun JDK 1.6

## Licence
Copyright (C) 2012  INRIA, All Rights Reserved
This program is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License for more details.
You should have received a copy of the GNU General Public License along with this program.  If not, see <http://www.gnu.org/licenses/>.

If you want to use this library under another licence please contact us contact(at)javascool.fr

## Security Warning
Open the user file system to the web can cause many security problem, so this library is originaly written to run on Java's Cool environment (http://www.javascool.fr) and make stuff to check if it's into.
However, you can fork the project, rewrite security checks into the Applet class, recompile and sign (With your own certificate) the final jar.