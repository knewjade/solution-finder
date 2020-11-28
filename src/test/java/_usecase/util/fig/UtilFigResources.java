package _usecase.util.fig;

public enum UtilFigResources {
    TetfuCase1("figs/tetfu_case1.gif"),
    TetfuCase2("figs/tetfu_case2.gif"),
    TetfuCase3("figs/tetfu_case3.gif"),
    TetfuCase4("figs/tetfu_case4.gif"),
    TetfuCase5("figs/tetfu_case5.gif"),
    TetfuCase6("figs/tetfu_case6.gif"),
    TetfuCase7("figs/tetfu_case7.gif"),
    TetfuCase8("figs/tetfu_case8.gif"),
    TetfuCase9("figs/tetfu_case9.gif"),
    TetfuCase10("figs/tetfu_case10.gif"),
    TetfuCase11("figs/tetfu_case11.gif"),
    FileCase1("figs/file_case1.gif"),
    FileCase2("figs/file_case2.gif"),
    PngCase1Java8("figs/png_case1_java8/"),
    PngCase1Java9("figs/png_case1_java9/"),
    PngCase1Java11("figs/png_case1_java9/"),  // Java9と同じ
    PngCase2Java8("figs/png_case2_java8/"),
    PngCase2Java9("figs/png_case2_java9/"),
    PngCase2Java11("figs/png_case2_java9/"),  // Java9と同じ
    ;

    private final String resourceName;

    UtilFigResources(String resourceName) {
        this.resourceName = resourceName;
    }

    public String getResourceName() {
        return resourceName;
    }
}
