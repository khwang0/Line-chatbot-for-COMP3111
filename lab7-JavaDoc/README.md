# Lab - 7: JavaDoc
Javadoc is a tool for generating API documentation in HTML format from doc comments in source code. In this lab, we are going to learn how to write doc comments and generate html documents using JavaDoc for our Java programs.

You can refer to a turtorial for writing doc comment:
[https://www.tutorialspoint.com/java/java_documentation.htm](https://www.tutorialspoint.com/java/java_documentation.htm).
Or the official documentation for JavaDoc:
[http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html](http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html)
## Doc Comments
Doc comments are special Java comments wrapped with `/** ... */`. **A doc comment must precede a class, field, constructor or method declaration.** JavaDoc tool can identify doc comments and automatically generate documentation in html based on the doc comments.

Below is an example of doc comment:
```
/**
 * Returns an Image object that can then be painted on the screen. 
 * The url argument must specify an absolute {@link URL}. The name
 * argument is a specifier that is relative to the url argument. 
 * 
 * This method always returns immediately, whether or not the 
 * image exists. When this applet attempts to draw the image on
 * the screen, the data will be loaded. The graphics primitives 
 * that draw the image will incrementally paint on the screen. 
 *
 * @param  url  an absolute URL giving the base location of the image
 * @param  name the location of the image, relative to the url argument
 * @return      the image at the specified URL
 * @see         Image
 */
 public Image getImage(URL url, String name) {
        try {
            return getImage(new URL(url, name));
        } catch (MalformedURLException e) {
            return null;
        }
 }
```
A doc comment consists of two parts: A description and block tags. In this example, the block tags are `@param`, `@return`, and `@see`.

More tags and their introductions can be found in this [turtorial](https://www.tutorialspoint.com/java/java_documentation.htm) or [official document](http://www.oracle.com/technetwork/java/javase/documentation/index-137868.html#tag)

## Run JavaDoc

To generate JavaDoc for a java file/package, you can use the following command:
```
javadoc -d [path to javadoc destination directory] [file name/package name]
```

For example, if you want to generate java docs for JavaDocLab.java and output the generated documents to folder docs/ you may run:
```
javadoc -d docs JavaDocLab.java
```

In this lab, we would like you to also generate documentation for private members in JavaDocLab.java, so please add option `-private`:
```
javadoc -private -d docs JavaDocLab.java
```
By executing this command, documents for class JavaDocLab should be generated in folder docs/.

## Your Tasks

1. Download JavaDocLab.java
2. Finish all the 6 `TODO` tasks in the file by rewriting the comments and turning them into doc comments so that JavaDoc tool can identify these comment blocks. (**Note: you should add proper tags as suggested by each TODO task instruction**).After finishing the tasks, please delete the task instruction.
3. Generate html documents for JavaDocLab.java by executing:
```
javadoc -private -d docs JavaDocLab.java
```
## Demo
Show us your generated documentations for class JavaDocLab and class Student.
