package haplous.rest.services;

public class ExcelUtil {

    //Method to check if the test case has to be executed
    public static boolean isExecutable(String sheetname, int rowNum, Xls_Reader suite) {

        if (suite.getCellData(sheetname, "RunMode", rowNum).equals("Y"))
            return true;
        else
            return false;
    }

    //Method to get the starting row of the test case
    public static int getRowNum(String sheetName, String colValue, Xls_Reader suite) {

        int rowNum;
        for (rowNum = 2; rowNum < suite.getRowCount(sheetName); rowNum++) {
            if (suite.getCellData(sheetName, "TCID", rowNum).equals(colValue))
                break;
        }
        return rowNum;
    }

    public static String getRandcellData(String sheetName, int rowNum, Xls_Reader suite,String columnName) {
        return suite.getCellData(sheetName, columnName, rowNum);
    }
    //Method to get the RunMode
    public static String getRunMode(String sheetName, int rowNum, Xls_Reader suite) {
        return suite.getCellData(sheetName, "RunMode", rowNum);
    }

    //Method to read the test case data
    public static Object[][] getData(String sheetName, Xls_Reader suite) {
        String[][] testcase = null;
        int testCount = suite.getRowCount(sheetName);
        testcase = new String[testCount - 1][11];
        int k = 0;
        for (int j = 2; j <= testCount; j++, k++) {
            testcase[k][0] = suite.getCellData(sheetName, "TCID", j);
            testcase[k][1] = suite.getCellData(sheetName, "Desciption", j);
            testcase[k][2] = suite.getCellData(sheetName, "Endpoint_Action", j);
            testcase[k][3] = suite.getCellData(sheetName, "RequestBody", j);
            testcase[k][4] = suite.getCellData(sheetName, "RequestBodyParam", j);
            testcase[k][5] = suite.getCellData(sheetName, "QueryParam", j);
            testcase[k][6] = suite.getCellData(sheetName, "SchemaAssertion", j);
            testcase[k][7] = suite.getCellData(sheetName, "ResponseBodyAssertion", j);
            testcase[k][8] = suite.getCellData(sheetName, "DataBaseAsertion", j);
            testcase[k][9] = suite.getCellData(sheetName, "RunMode", j);
            testcase[k][10] = suite.getCellData(sheetName, "Persist", j);

        }
        return testcase;
    }

    //Method to read the test suite
    public static Object[][] excelSuite(String sheetName, Xls_Reader suite) {

        String[][] testcase = null;
        int testCount = suite.getRowCount(sheetName);
        testcase = new String[testCount - 1][3];
        int k = 0;
        for (int j = 2; j <= testCount; j++, k++) {
            testcase[k][0] = suite.getCellData(sheetName, "ModuleName", j);
            testcase[k][1] = suite.getCellData(sheetName, "RunMode", j);
            System.out.println(testcase[k][1]);
            testcase[k][2] = suite.getCellData(sheetName, "Tags", j);
        }
        return testcase;
    }

}
