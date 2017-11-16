import java.lang.StringBuilder;

/**
 * A representation of the result from a class.
 *
 * In this context, {@link #gradePoints()} refers to the grade points corresponding to the assigned
 * grade, e.g. a B corresponds to a grade point of 3.0.
 */
class ClassResult {
    private final String classCode;
    private final String className;
    private final double gradePoints;
    private final int credits;

    public ClassResult(
            String classCode, String className, double gradePoints, int credits) {
        this.classCode = classCode;
        this.className = className;
        this.gradePoints = gradePoints;
        this.credits = credits;
    }

    public String classCode() { return classCode; }
    public String className() { return className; }
    public double gradePoints() { return gradePoints; }
    public int credits() { return credits; }
}

/**
 * A representation of a transcript for a single semester.
 *
 * The {@link generateTranscriptForWidth} method prints the transcript to stdout. To determine the
 * number of lines the transcript will take before printing, {@link transcriptHeightForWidth} may
 * be used.
 */
class Transcript {
    private final String studentName;
    private final String studentId;
    private final String semesterName;
    private final ClassResult[] classResults;

    public Transcript(
            String studentName,
            String studentId,
            String semesterName,
            ClassResult[] classResults) {
        this.studentName = studentName;
        this.studentId = studentId;
        this.semesterName = semesterName;
        this.classResults = classResults;
    }

    public String studentName() { return studentName; }
    public String studentId() { return studentId; }
    public String semesterName() { return semesterName; }
    public ClassResult[] classResults() { return classResults; }

    /**
     * Processes a single ClassResult field.
     *
     * FIXME: This method isn't working! What's going on?!
     */
    private static void processClassResultField(
            String field,
            int currentLength,
            int totalLines,
            int width,
            StringBuilder transcriptBuilder) {
        if (currentLength + 1 + field.length() > width) {
            totalLines++;
            if (transcriptBuilder != null) {
                transcriptBuilder.append("\n    ");
            }
            currentLength = 4;
        } else {
            if (transcriptBuilder != null) {
                transcriptBuilder.append(" ");
            }
            currentLength++;
        }

        if (transcriptBuilder != null) {
          transcriptBuilder.append(field);
        }
        currentLength += field.length();
    }

    /**
     * Prints the transcript to stdout.
     *
     * We design the transcript with the following format:
     * 1. First, the student name, ID, and semester name are printed on separate lines and centered
     *    relative to the provided width.
     * 2. Next, each class result is printed on a separate line. In the case that a line will
     *    exceed the width limit, the word is shifted to the next line with four-space indentation:
     *
     *        COMP3111
     *            Software Engineering
     *            4.00 4
     *
     * 3. Finally, the semester GPA is printed.
     *
     * @param width The preferred width for each line, although this may not be respected if an
     *              individual field is too long.
     */
    public void generateTranscriptForWidth(int width) {
        StringBuilder transcriptBuilder = new StringBuilder();

        // By finding the remaining half of the spaces, we center align the text.
        int spacesBeforeName = (width - studentName.length()) / 2;
        for (int i = 0; i < spacesBeforeName; ++i) {
            transcriptBuilder.append(" ");
        }
        transcriptBuilder.append(studentName);
        transcriptBuilder.append("\n");

        int spacesBeforeId = (width - studentId.length()) / 2;
        for (int i = 0; i < spacesBeforeId; ++i) {
            transcriptBuilder.append(" ");
        }
        transcriptBuilder.append(studentId);
        transcriptBuilder.append("\n");

        int spacesBeforeSemester = (width - semesterName.length()) / 2;
        for (int i = 0; i < spacesBeforeSemester; ++i) {
            transcriptBuilder.append(" ");
        }
        transcriptBuilder.append(semesterName);
        transcriptBuilder.append("\n");

        // Newline between header and transcript body.
        transcriptBuilder.append("\n");

        double totalGradePoints = 0.0;
        int totalCredits = 0;
        for (ClassResult result : classResults) {
            // Accumulate the total grade points for later display.
            totalGradePoints += result.gradePoints() * result.credits();
            totalCredits += result.credits();

            int currentLength = result.classCode().length();
            transcriptBuilder.append(result.classCode());

            // Since totalLines doesn't matter here, we just set it to 0.
            int totalLines = 0;
            processClassResultField(
                    result.className(), currentLength, totalLines, width, transcriptBuilder);

            String gradePointsString = String.format("%.2f", result.gradePoints());
            processClassResultField(
                    gradePointsString, currentLength, totalLines, width, transcriptBuilder);

            String creditsString = String.format("%d", result.credits());
            processClassResultField(
                    creditsString, currentLength, totalLines, width, transcriptBuilder);

            transcriptBuilder.append("\n");
        }

        // Newline between the transcript and the summary.
        transcriptBuilder.append("\n");

        transcriptBuilder.append(
                String.format("Semester GPA: %.2f", totalGradePoints / totalCredits));

        System.out.println(transcriptBuilder.toString());
    }

    /**
     * Returns the total number of lines required to display the transcript.
     *
     * For more information about the transcript format, see {@link generateTranscriptForWidth}.
     *
     * @param width The preferred width for each line.
     * @return The total number of lines required to display the transcript with respect to the
     *         {@code width} argument.
     */
    public int transcriptHeightForWidth(int width) {
        // The header consists of studentName, studentId, semesterName, and separating newline.
        int totalLines = 4;

        for (ClassResult result : classResults) {
            int currentLength = result.classCode().length();
            processClassResultField(
                    result.className(), currentLength, totalLines, width, null);

            String gradePointsString = String.format("%.2f", result.gradePoints());
            processClassResultField(
                    gradePointsString, currentLength, totalLines, width, null);

            String creditsString = String.format("%d", result.credits());
            processClassResultField(
                    creditsString, currentLength, totalLines, width, null);

            totalLines++;
        }

        // The footer consists of the line separating the transcript body and the line indicating
        // the semester GPA.
        totalLines += 2;
        return totalLines;
    }

}

public class RefactoringLabTask3 {
    public static void main(String[] args) {
        ClassResult[] classResults = new ClassResult[] {
            new ClassResult("COMP3111", "Software Engineering", 4.0, 4),
            new ClassResult("COMP3311", "Database Management Systems", 3.3, 3),
        };
        Transcript transcript = new Transcript("John Chan", "21039408", "2017F", classResults);

        // We call transcriptHeightForWidth first to figure out how large it's going to be.
        System.out.println("Total lines: " + transcript.transcriptHeightForWidth(20));
        transcript.generateTranscriptForWidth(20);
    }
}
