# Lab 6 Solutions

## Task 1

For the first task, there are two code smells that make the center-alignment
code hard to change. 

First, the character used to center align the text is repeated six times.
Naturally, it's easy to forget to change one of these, resulting in buggy code.
One refactoring that can address this is to pull the character out into a 
constant, like so:

```java
private static final char CENTER_ALIGNMENT_CHAR = '-';
```

The literals can then be replaced with this constant where necessary.

The second code smell is that at a coarser granularity, we repeat ourselves on
_how_ we center-align the code. Pulling this out into a helper method can
remedy this problem.

## Task 2

The idea behind this task is that we have *two* literals trying to represent
the same thing in *two* different places. Specifically, whenever we choose to
indent, we add `"\n    "` and update the current position in the line to
reflect this - we set `currentLength` to `4`. Furthermore, we share the logic
concerning the current line position in both _generate_ and _height_ methods.

Both of these smells resulted in a number of student submissions being
incorrect - some students neglected to update the `currentLength` assignment in
either one or both of the affected methods.

To address these smells, the first thing we can do is pull out the amount of 
indentation into a constant, like before. The second thing we might notice is
that the amount of indentation can be used to determine the spaces that need to
be generated. Putting this all together, we can first define the constant, like
so:

```java
// declare the amount of indentation as a constant
private static final int NUM_INDENT = 2;
```

Replacing literals with the constant where necessary. For our second
observation, we can update the _generate_ method as follows:

```java
currentLength = NUM_INDENT;
transcriptBuilder.append('\n');
for (int i = 0; i < NUM_INDENT; ++i) {
  transcriptBuilder.append(' ');
}
```

This way, we don't need to clumsily update the string literal, or have a
ridiculously long string if we want a lot of indentation. Defining such a
string is an error-prone practice in itself.

```java
// feels like we're looking for trouble with 16 consecutive spaces?!
private static final String INDENT_STRING = "\n                ";
```

As with before, pulling these out into methods are a good way to modularize the
code.

## Task 3 (Updated Version)

The updated version of Task 3 required students to identify the problem with a
colleague's refactoring, and to fix it. Running the code, you would find that
the number of lines didn't look quite right. And printing `currentLength` after
each invocation of the refactored method, you would find that the variable
never gets updated.

The reason for this is that Java does not pass primitives like `int`s by
reference. So, you'd only be updating the _local_ variable inside of the
method, but not the variable being passed to the method.

To update this, we can conveniently employ Martin Fowler's
[parameter object](https://refactoring.guru/introduce-parameter-object)
technique to wrap the primitives into an object. Specifically, we can wrap the
formatter state into an object that looks like:

```java
class TranscriptFormatterState {
  public int currentLength;
  ‎public int totalLines;
  ‎public StringBuilder transcriptBuilder;
}
```

Then when either method is called, we create one such object and pass it to any
of our helper methods, which should be updated to talk to this formatter
object.

More discussion of the advantages of this refactoring below.

## Task 3 (Old Version)

The old version of Task 3 required students to find some way to consolidate or
share code between the _generate_ and _height_ methods. The premise of this is
that both methods essentially follow the same steps, especially the tricky
indentation calculation, except that one method counts the number of lines
while the other prints out the string stored in the `transcriptBuilder`.

One opportunity for code sharing can be found in the updated Task 3 discussed
above - the indentation logic can be consolidated into a single helper method
that is used by both the _generate_ and _height_ methods. While this improves
code reuse, the refactoring feels oddly unsatisfying. Specifically, layout
changes to the transcript (e.g. an extra empty line between the header and body
of the transcript) would _still_ require changes in both the _generate_ and
_height_ methods.

The option I personally like involves combining the behavior of the two into
a single helper method:

```java
private int transcriptHeightForWidth(int width, boolean shouldPrint) {
    StringBuilder transcriptBuilder = new StringBuilder();

    // NOTE: addCenterAlignedLabel() takes a transcriptBuilder, width, and
    //       label, adding the center-aligned label to the transcriptBuilder.
    addCenterAlignedLabel(transcriptBuilder, width, studentName);
    addCenterAlignedLabel(transcriptBuilder, width, studentId);
    addCenterAlignedLabel(transcriptBuilder, width, semesterName);

    // newline between header and transcript body
    transcriptBuilder.append('\n');

    // NOTE: initialize totalLines with the lines in the header section
    int totalLines = 4;

    double totalGradePoints = 0.0;
    int totalCredits = 0;
    for (ClassResult result : classResults) {
        totalGradePoints += result.gradePoints() * result.credits();
        totalCredits += result.credits();

        int currentLength = result.classCode().length();
        transcriptBuilder.append(result.classCode());

        if (currentLength + 1 + result.className().length() > width) {
            currentLength = NUM_INDENT;
            transcriptBuilder.append('\n');
            for (int i = 0; i < NUM_INDENT; ++i) {
              transcriptBuilder.append(' ');
            }

            // NOTE: also add to the total lines whenever there is a new line
            totalLines++;
        } else {
            transcriptBuilder.append(' ');
            currentLength++;
        }

        transcriptBuilder.append(result.className());
        currentLength += result.className().length();

        String gradePointsString = String.format("%.2f", result.gradePoints());
        if (currentLength + 1 + gradePointsString.length() > width) {
            currentLength = NUM_INDENT;
            transcriptBuilder.append('\n');
            for (int i = 0; i < NUM_INDENT; ++i) {
              transcriptBuilder.append(' ');
            }
            totalLines++;
        } else {
            transcriptBuilder.append(' ');
            currentLength++;
        }

        transcriptBuilder.append(gradePointsString);
        currentLength += gradePointsString.length();

        String creditsString = String.format("%d", result.credits());
        if (currentLength + 1 + creditsString.length() > width) {
            currentLength = NUM_INDENT;
            transcriptBuilder.append('\n');
            for (int i = 0; i < NUM_INDENT; ++i) {
              transcriptBuilder.append(' ');
            }
            totalLines++;
        } else {
            transcriptBuilder.append(' ');
            currentLength++;
        }

        transcriptBuilder.append(creditsString);
        currentLength += creditsString.length();

        transcriptBuilder.append('\n');
        totalLines++;
    }

    // newline between the transcript and the summary
    transcriptBuilder.append('\n');

    transcriptBuilder.append(String.format(
            "Semester GPA: %.2f",
            totalGradePoints / totalCredits));

    // NOTE: add the two footer lines to the count
    totalLines += 2;

    if (shouldPrint) {
        System.out.println(transcriptBuilder.toString());
    }

    return totalLines;
}
```

Then inside of the _generate_ and _height_ methods, we can call them like so:

```java
public void generateTranscriptForWidth(int width) {
    transcriptHeightForWidth(width, true);
}

public int transcriptHeightForWidth(int width) {
    return transcriptHeightForWidth(width, false);
}
```

The upside of this approach is that the two methods, being structurally linked,
can now be simply updated in a single location. The downside is that we lose
the separation we had earlier - now we need to understand both the layout code
*and* the line counting code within a single method.

Some observant students may notice that we need to repeat the work twice if we
want to get the desired height *then* print the transcript. To avoid this work,
we can cache results in a private `Map<Integer, String>` that we can check
before generating a transcript.
