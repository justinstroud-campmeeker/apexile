public class AnnotatedClass {
    @AuraEnabled
    public static String getName() {
        return 'Test Name';
    }
    
    @TestMethod
    static void testMethod() {
        String result = getName();
        System.assertEquals('Test Name', result);
    }
}